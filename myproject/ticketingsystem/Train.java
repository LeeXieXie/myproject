package ticketingsystem;

import java.util.concurrent.locks.ReentrantLock;

class Seat{
    private int seatId;//座位id
    int seatInuse; //占座flag
    private ReentrantLock lock; //可重入锁

    public Seat(int id){
        seatId = id;
        seatInuse = 0x0;//初始值为0x0,即未占用
        lock = new ReentrantLock();
    }


    //查询座位是否有空座位，有的话返回true
    //查询空位的方法
            /*
            标识火车占用区间的核心是经过的区间二进制位1
            1<<arrival 例如arrival = 6,那么1<<6 就是 0001000000;
            1<<departure 例如departure = 2, 那么1<<2 就是0000000100;
            (1<<arrival)-(1<<departure) = 0001000000 - 0000000100;
            0001000000
          - 0000000100
          ------------
            0000111100
            而第i位为1,表示该车站范围在[i,i+1]
             */
    //如果seatInuse & ((1<<arrival) - (1<<departure)) == 0,那么就表示当前座位未被占用,那么我可以占用这个
    public boolean seatInquery(int departure, int arrival){
        int interval = ((1<<arrival) - (1<<departure));
        lock.lock();
        try {
            if((interval & this.seatInuse)==0){
                return true;
            }
        }finally {
            lock.unlock();
        }
        return false;
    }

    //占座
    public boolean seatBuy(int departure, int arrival){
        int interval = ((1<<arrival) - (1<<departure));
//        System.out.println("seatBuy");
        if((interval & this.seatInuse)==0) {
            lock.lock();
            try {
                if ((interval & this.seatInuse) == 0) {
                    this.seatInuse = (interval | this.seatInuse);
                    return true;
                }
            } finally {
                lock.unlock();
            }
        }
        return false;
    }

    //退座
    //解锁方法
            /*
            (1<<arrival)-(1<<departure) = 0000111100
            trainSeat[seatId].seatInuse.get() = 0000111100
            ~((1<<arrival) - (1<<departure)) = 1111000011
            trainSeat[seatId].seatInuse.get() & (~((1<<arrival) - (1<<departure)))
            0000111100
           &11110000011
           ------------
            00000000000

            即把trainSeat[seatId].seatInuse.get() = 0x0
             */
    public boolean seatRefund(int departure, int arrival){
        int interval = ((1<<arrival) - (1<<departure));
        lock.lock();
        try {
            if ((interval & seatInuse) == 0)
                return false;
            else {
                seatInuse = (seatInuse & (~interval));
                return true;
            }
        }finally {
            //System.out.println(1);
            lock.unlock();
        }
    }
}

public class Train {
    private int seatNum;//火车的总座位数
    /*
    * Seat[1][2] 表示1号车厢的第2号座位
     */
    private Seat[][] trainSeat;//火车的座位
    private int routenum;//路线数
    private int coachnum ;// 表示车厢数
    private int seatnum;//座位数
    private int stationnum;// 表示车站数目

    public Train(int route, int coach, int seat, int station){
        this.routenum=route;
        this.coachnum=coach;
        this.stationnum=station;
        this.seatnum = seat;
        this.seatNum = coach * seat;//每个火车内的座位数目
        trainSeat = new Seat[coachnum + 1][seatNum + 1];//序号从1开始
        for(int i = 1; i <= coachnum; i ++) {
            for (int j = 1; j <= seatnum; j ++)
                trainSeat[i][j] = new Seat(j);//把每个座位都实例化
        }
    }


    public Ticket seatLock(String passenger, int route, long tId, int departure, int arrival) {

        if (departure >= 1 && departure <arrival  && arrival <= this.stationnum ) {
            Ticket ticket = new Ticket();
            //遍历所有的位置，座位有空，那就可以购买成功，那就上锁
//            System.out.println(coachnum);
            for (int i = 1; i <= coachnum; i++) {
//                System.out.println(1);
                for (int j = 1; j <= seatnum; j++) {
//                    System.out.println(j);
                    if (trainSeat[i][j].seatBuy(departure, arrival)){
                        ticket.passenger = passenger;
                        ticket.route = route;
                        ticket.tid = tId;
                        ticket.departure = departure;
                        ticket.arrival = arrival;
                        ticket.coach = i;
                        ticket.seat = j;
                        return ticket;
                    }
                }
            }
        }
        return null;
    }
    public boolean seatUnlock(int coachId, int seatId, int departure, int arrival){
//        System.out.println(coachId);
        if (departure >= 1 && departure <arrival  && arrival <= this.stationnum) {
            if(trainSeat[coachId][seatId].seatInquery(departure, arrival)){
                //该位置没有人使用，那么就不需要解锁了
                return false;
            }else{
                return trainSeat[coachId][seatId].seatRefund(departure, arrival);
                //return true;
            }
        }
        return  false;
    }

    public int seatQuery(int departure, int arrival){
        if (departure >= 1 && departure <arrival  && arrival <= this.stationnum) {
            int remainSeat = 0;//余票个数
            for (int i = 1; i <= coachnum; i++) {
                for (int j = 1; j <= seatnum; j ++)
                    if (trainSeat[i][j].seatInquery(departure, arrival))
                        remainSeat++;
            }
            return remainSeat;
        }
        return 0;
    }


    public boolean isIllegalinput(int route, int departure, int arrival){
        //判断是否非法输入
        if ((departure > 0) && (departure < arrival) && (arrival <= this.stationnum) && (route > 0) && (route <= this.routenum))
            return true;
        else
            return false;
    }

}


//package ticketingsystem;
//
//        import java.util.concurrent.ConcurrentHashMap;
//        import java.util.concurrent.locks.ReentrantLock;
//
//        import static java.lang.System.exit;
//
//public class Train {
//
//    public boolean[][] seatStatus; // 表示每节车厢车座的状态，True-表示已占用，Faslse-表示空着
//    public int seatNum;         // 表示列车座位的总数
//    public int coachNum;        // 表示车厢数
//    public int stationNum;      // 表示车站数目
//    private ReentrantLock lock; // 列车锁，可重入锁
//
//    private ConcurrentHashMap<Long, Ticket> soldTickets;//已售车票
//
//    public Train(int seatnum, int coachnum, int stationnum) {
//
//        seatNum = coachnum * seatnum;
//        coachNum = coachnum;
//        stationNum = stationnum;
//        lock = new ReentrantLock();
//        soldTickets = new ConcurrentHashMap<Long, Ticket>();
//
//        seatStatus = new boolean[seatNum + 1][stationNum + 1];
//        for (int i = 1; i <= seatNum; i++) {
//            for (int j = 1; j <= stationNum; j++) {
//                seatStatus[i][j] = new Boolean(false);
//            }
//        }
//    }
//
//    public int seatLock(int seatNum, int departure, int arrival) {
//        int j;
//        int i = 0;
//        if (departure < 1 || departure > stationNum || arrival < 1 || arrival > stationNum || departure >= arrival)
//            exit(0);
//        else {
//            for (i = 1; i <= seatNum; i++) {
//                for (j = departure; j < arrival; j++) {
//                    lock.lock();
//                    try {
//                        if (!seatStatus[i][j])
//                            seatStatus[i][j] = true;
//                    } finally {
//                        lock.unlock();
//                    }
//                }
//            }
//        }
//        return i;
//    }
//
//    public boolean seatUnlock(int seat, int departure, int arrival) {
//        if (departure < 1 || departure > stationNum || arrival < 1 || arrival > stationNum || departure >= arrival)
//            return false;
//        else {
//            for(int i = departure; i < arrival; i ++)
//            {
//                lock.lock();
//                try {
//                    if (seatStatus[seat][i] == false)
//                        seatStatus[seat][i] = true;
//                } finally {
//                    lock.unlock();
//                }
//            }
//            return true;
//        }
//    }
//
//    public int seatQuery(int seatNum, int departure, int arrival){
//        int usedSeat = 0;
//        if (departure < 1 || departure > stationNum || arrival < 1 || arrival > stationNum || departure >= arrival)
//            exit(0);
//        else {
//            int j;
//            int i;
//            for (i = 1; i <= seatNum; i++) {
//                for (j = departure; j < arrival; j++) {
//                    lock.lock();
//                    try {
//                        if (seatStatus[i][j] == true)
//                            usedSeat++;
//                    } finally {
//                        lock.unlock();
//                    }
//                }
//            }
//        }
//        return (seatNum - usedSeat);
//    }
//}

