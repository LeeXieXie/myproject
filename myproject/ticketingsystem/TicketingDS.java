package ticketingsystem;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TicketingDS implements TicketingSystem {

    private int routenum = 5;
    private int coachnum = 8;
    private int seatnum = 100;
    private int stationnum = 10;
    private int threadnum = 16;
    private  int seatNum = coachnum * seatnum;
    private Train[] trains;
    private AtomicLong ticketID;
    private ConcurrentHashMap<Long, Ticket> soldTicket;
    public TicketingDS(int routenum, int coachnum, int seatnum, int stationnum, int threadnum){
        //火车/路线
        this.trains = new Train[routenum + 1];
        this.routenum=routenum;
        this.coachnum=coachnum;
        this.seatnum=seatnum;
        this.stationnum=stationnum;
        this.threadnum=threadnum;
        this.seatNum=this.seatnum*this.coachnum;
        //原子操作每次加1
        this.ticketID = new AtomicLong(1);
        for(int i = 1; i <= routenum; i ++){
            trains[i] = new Train(i, coachnum, seatnum, stationnum);//实例化每辆火车
        }
        soldTicket = new ConcurrentHashMap<Long, Ticket>();//初始化已售票hash表
    }

    @Override
    public Ticket buyTicket(String passenger, int route, int departure, int arrival) {
        if(trains[route].isIllegalinput(route, departure,arrival)){
            long tId = ticketID.getAndIncrement();
            Ticket ticket = trains[route].seatLock(passenger, route, tId, departure, arrival);
            return ticket;
        }
        return null;
    }

    @Override
    public int inquiry(int route, int departure, int arrival) {
        if(trains[route].isIllegalinput(route, departure, arrival))
            return trains[route].seatQuery(departure, arrival);
        return 0;
    }

    @Override
    public boolean refundTicket(Ticket ticket) {
        if(trains[ticket.route].isIllegalinput(ticket.route, ticket.departure, ticket.arrival)) {
            //如果已售票hash表内没有这个票号
           if (!soldTicket.containsKey(ticket.tid) || !ticket.equals(soldTicket.get(ticket.tid)))
               return false;
            else {
                //还原座位号
                int seatId = ticket.seat;
                int coachId = ticket.coach;
//                System.out.println(seatId);
//                System.out.println(coachId);
                if (trains[ticket.route].seatUnlock(coachId, seatId, ticket.departure, ticket.arrival)) {
                    soldTicket.remove(ticket.tid, ticket);
                    return true;
               }
            }
        }
        return false;
    }

    @Override
    public boolean buyTicketReplay(Ticket ticket) {
        return false;
    }

    @Override
    public boolean refundTicketReplay(Ticket ticket) {
        return false;
    }


}
