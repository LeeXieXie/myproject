// package ticketingsystem;
//
// import java.util.ArrayList;
// import java.util.Random;
// import java.util.concurrent.atomic.AtomicInteger;
// import java.util.concurrent.atomic.AtomicLong;
//
// public class Test {
//
// 	final static int[] threadnums = {4, 8, 16, 32, 64}; // 并发购票线程的数量
// 	//routenum 是车次总数（缺省为 5），coachnum 是每次列车的车厢数目（缺省为 8），seatnum 是每节车厢的座位数（缺省为 100），stationnum 是每个车次经停站的数量（缺省为 10，含始发站和终点站）
// 	final static int routenum = 5; // routenum 是车次总数（缺省为 5）
// 	final static int coachnum = 8; // coachnum 是每次列车的车厢数目（缺省为 8）
// 	final static int seatnum = 100; // seatnum 是每节车厢的座位数（缺省为 100）
// 	final static int stationnum = 10; // stationnum 是每个车次经停站的数量（缺省为 10，含始发站和终点站）
//
// 	final  static int thread = 10000;
// 	//三种方法若干次（缺省为总共 10000 次）
// 	final static int testnum = 50000;
// 	// 60% 查询余票、30% 购票和 10% 退票
// 	final static int refundTicketpercent = 10; //10% 退票
// 	final static int buyTicketpercent = 30; //30% 购票
// 	final static int inQuerypercent = 60; //60% 查询余票
//
// 	private static long[] refundNum = new long[thread];//退票总数
// 	private static long[] buyNum = new long[thread];//买票总数
// 	private static long[] queryNum = new long[thread];//查询总数
// 	static long[] refundTime = new long[thread];//退票时间
// 	static long[] buyTime = new long[thread];//买票时间
// 	static long[] queryTime = new long[thread];//查询时间
//
// 	static final AtomicInteger threadId = new AtomicInteger(0);
//
// 	static final AtomicLong totalrefundTicketNum = new AtomicLong(0);
// 	static final AtomicLong totalbuyTicketNum = new AtomicLong(0);
// 	static final AtomicLong totalinQueryNum = new AtomicLong(0);
//
// 	static final AtomicLong totalrefundTicketTime = new AtomicLong(0);
// 	static final AtomicLong totalbuyTicketTime = new AtomicLong(0);
// 	static final AtomicLong totalinQueryTime = new AtomicLong(0);
// 	static String passengerName() {
// 		//随机生成passenger
// 		Random rand = new Random();
// 		long uid = rand.nextInt(testnum);//生成[0,testnum)内随机整数序列
// 		return "passenger" + uid;
// 	}
//
// 	public static void main(String[] args) throws InterruptedException {
//
// 		for (int threadnum = 0; threadnum < threadnums.length; threadnum++) {//遍历执行4, 8, 16, 32, 64个线程
// 			final TicketingDS tds = new TicketingDS(routenum, coachnum, seatnum, stationnum, threadnums[threadnum]);
// 			Thread[] threads = new Thread[threadnums[threadnum]];//创建线程数组
// 			for (int i = 0; i < threadnums[threadnum]; i++) {
// 				threads[i] = new Thread(new Runnable() {
// 					@Override
// 					public void run() {
// 						Random rand = new Random();//随机对象
// 						Ticket ticket = new Ticket();
// 						int id = threadId.getAndIncrement();
// 						ArrayList<Ticket> soldTicket = new ArrayList<Ticket>();
// 						long begin ;
// 						//10000的对票操作
// 						for (int i = 0; i < testnum; i++) {
// 							int timeT = rand.nextInt(100);//取1到100的随机数
// 							//退票测试
// 							if (0 <= timeT && timeT < refundTicketpercent && soldTicket.size() > 0) {
// 								int select = rand.nextInt(soldTicket.size());
// 								if ((ticket = soldTicket.remove(select)) != null) {
// 									begin = System.nanoTime();
// 									tds.refundTicket(ticket);
// 									refundTime[id] += (System.nanoTime() - begin);
// 									++ refundNum[id];
// //									System.out.println(refundNum[id]);
// //									System.out.println(refundTime[id]);
// //									if (!result) {
// //										System.out.println("Refund ErrO-_-");
// //										System.out.flush();
// //									}
// 								}
// 								else{
// 									System.out.println("Refund ErrO-_-");
// 									System.out.flush();
// 								}
// 							}
// 							//买票测试
// 							else if (refundTicketpercent <= timeT && timeT < (buyTicketpercent)) {
// 								String passenger = passengerName();
// 								int route = rand.nextInt(routenum) +1 ;//列车车次的随机值
// 								int departure = rand.nextInt(stationnum - 1) + 1;//随机出发站
// 								int arrival = departure + rand.nextInt(stationnum - departure) + 1;//随机终点站
// 								//买票时间测试
// 								begin = System.nanoTime();
// 								ticket = tds.buyTicket(passenger, route, departure, arrival);
// 								//耗时计算
// 								buyTime[id] += System.nanoTime() - begin;
// 								++ buyNum[id];
// 								if (ticket != null) {
// 									soldTicket.add(ticket);
// 								}
// 							}
// 							//查票测试
// 							else if ((buyTicketpercent) <= timeT && timeT < 100) {
// 								int route = rand.nextInt(routenum) + 1;//列车车次的随机值
// 								int departure = rand.nextInt(stationnum - 1) + 1;//随机出发站
// 								int arrival = departure + rand.nextInt(stationnum - departure) + 1;//随机终点站
// 								//查票测试
// 								begin = System.nanoTime();
// 								tds.inquiry(route, departure, arrival);
// 								queryTime[id] = System.nanoTime() - begin;
// 								++ queryNum[id];
//								totalinQueryNum.addAndGet(queryNum[id]);//所有线程查询次数总和
//								totalinQueryTime.addAndGet(queryTime[id]);//所有线程查询时间总和
// 							}
// 						}
// 						totalrefundTicketNum.addAndGet(refundNum[id]);//所有线程退票次数总和
// 						totalbuyTicketNum.addAndGet(buyNum[id]);//所有线程买票次数总和
// 						totalinQueryNum.addAndGet(queryNum[id]);//所有线程查询次数总和
//
// 						totalrefundTicketTime.addAndGet(refundTime[id]);//所有线程退票时间总和
// 						totalbuyTicketTime.addAndGet(buyTime[id]);//所有线程买票时间总和
// 						totalinQueryTime.addAndGet(queryTime[id]);//所有线程查询时间总和
//
//
// //						System.out.println(totalrefundTicketTime.get());
// //						System.out.println(totalrefundTicketNum.get());
// //						System.out.println(totalrefundTicketTime.get()/totalrefundTicketNum.get());
// //
// //
// //						System.out.println(totalinQueryTime.get());
// //						System.out.println(totalinQueryNum.get());
// //						System.out.println(totalinQueryTime.get()/totalinQueryNum.get());
// 					}
// 				});//线程实例化结束
// 			}
// //
// 			//异步多线程的执行和计时
// 			long beginTime = System.currentTimeMillis();//获取毫秒级的当前时间
// 			//异步启动自己的run方法
// 			for (int i = 0; i < threadnums[threadnum]; i++) {
// 				threads[i].start();//启动线程
// 			}
// 			//所有线程阻塞主线程
// 			for (int i = 0; i < threadnums[threadnum]; i++) {
//				threads[i].join();
//			}
//
// 			long executionTime = System.currentTimeMillis() - beginTime;
// 			System.out.println("ThreadNum: " + threadnums[threadnum] + '\n' + ">>>"
// 					+ "Total Execution Time(ms): " + executionTime + '\n' + ">>>"
// 					+ "Throughput(kop/s): " + (double) (testnum * threadnums[threadnum]) / executionTime + '\n' + ">>>"
// 					+ "QueryTicket Average Time(ns): " + (double)(totalinQueryTime.get() / totalinQueryNum.get()) + '\n' + ">>>"
// 					+ "BuyTicket Average Time(ns): " + (totalbuyTicketTime.get() / totalbuyTicketNum.get()) + '\n' + ">>>"
// 					+ "RefundTicket Average Time(ns): " + (totalrefundTicketTime.get() / totalrefundTicketNum.get()));
// 			System.out.println("^^^^^^^^^^^^ThreadNum: " + threadnums[threadnum] + "^^^^^^^^^^^^");
//			System.out.println(totalinQueryTime.get());
//			System.out.println(totalinQueryNum.get());
// 		}
// 		totalrefundTicketNum.set(0);
// 		totalbuyTicketNum.set(0);
// 		totalinQueryNum.set(0);
// 		totalrefundTicketTime.set(0);
// 		totalbuyTicketTime.set(0);
// 		totalinQueryTime.set(0);
// 	}
// }




package ticketingsystem;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
public class Test {
	private final static int ROUTE_NUM = 5;
	private final static int COACH_NUM = 8;
	private final static int SEAT_NUM = 100;
	private final static int STATION_NUM = 10;

	private final static int TEST_NUM = 10000;
	private final static int refund = 10;
	private final static int buy = 40;
	private final static int query = 100;
	private final static int thread = 64;
	private final static long[] buyTicketTime = new long[thread];//买票时间
	private final static long[] refundTime = new long[thread];//退票时间
	private final static long[] inquiryTime = new long[thread];//查询时间

	private final static long[] buyTotal = new long[thread];//买票总数
	private final static long[] refundTotal = new long[thread];//退票总数
	private final static long[] inquiryTotal = new long[thread];//查询总数

	private final static AtomicInteger threadId = new AtomicInteger(0);

	static String passengerName() {
		//随机生成passenger
		Random rand = new Random();
		long uid = rand.nextInt(TEST_NUM);
		return "passenger" + uid;
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		final int[] threadNums = { 4, 8, 16, 32, 64 };
		List resultList = new ArrayList();
		CSVPrinter csvPrinter = null;
		Object[] objects = null;
		int p;

		for (p = 0; p < threadNums.length; ++p) {
			final TicketingDS tds = new TicketingDS(ROUTE_NUM, COACH_NUM, SEAT_NUM, STATION_NUM, threadNums[p]);
			Thread[] threads = new Thread[threadNums[p]];
			for (int i = 0; i < threadNums[p]; i++) {
				threads[i] = new Thread(new Runnable() {
					public void run() {
						Random rand = new Random();
						Ticket ticket;
						int id = threadId.getAndIncrement();
						ArrayList<Ticket> soldTicket = new ArrayList<>();
						for (int i = 0; i < TEST_NUM; i++) {
							int sel = rand.nextInt(query);
							if (0 <= sel && sel < refund && soldTicket.size() > 0) { // 退票
								int select = rand.nextInt(soldTicket.size());
								if ((ticket = soldTicket.remove(select)) != null) {
									long s = System.nanoTime();
									tds.refundTicket(ticket);
									long e = System.nanoTime();
									refundTime[id] += e - s;
									refundTotal[id] += 1;
								} else {
									System.out.println("ErrOfRefund2");
								}
							} else if (refund <= sel && sel < buy) { // 买票
								String passenger = passengerName();
								int route = rand.nextInt(ROUTE_NUM) + 1;
								int departure = rand.nextInt(STATION_NUM - 1) + 1;
								int arrival = departure + rand.nextInt(STATION_NUM - departure) + 1;
								long s = System.nanoTime();
								ticket = tds.buyTicket(passenger, route, departure, arrival);
								long e = System.nanoTime();
								buyTicketTime[id] += e - s;
								buyTotal[id] += 1;
								if (ticket != null) {
									soldTicket.add(ticket);
								}
							} else if (buy <= sel && sel < query) { // 查票
								int route = rand.nextInt(ROUTE_NUM) + 1;
								int departure = rand.nextInt(STATION_NUM - 1) + 1;
								int arrival = departure + rand.nextInt(STATION_NUM - departure) + 1;
								long s = System.nanoTime();
								tds.inquiry(route, departure, arrival);
								long e = System.nanoTime();
								inquiryTime[id] += e - s;
//								System.out.println(inquiryTime[id]);
								inquiryTotal[id] += 1;
							}
						}
					}
				});
			}
			long start = System.currentTimeMillis();
			for (int i = 0; i < threadNums[p]; ++i)
				threads[i].start();

			for (int i = 0; i < threadNums[p]; i++) {
				threads[i].join();
			}
			long end = System.currentTimeMillis();
			long buyTotalTime = addAndGet(buyTicketTime, threadNums[p]);
			long refundTotalTime = addAndGet(refundTime, threadNums[p]);
			long inquiryTotalTime = addAndGet(inquiryTime, threadNums[p]);

			double bTotal = (double) addAndGet(buyTotal, threadNums[p]);
			double rTotal = (double) addAndGet(refundTotal, threadNums[p]);
			double iTotal = (double) addAndGet(inquiryTotal, threadNums[p]);

			long buyAvgTime = (long) (buyTotalTime / bTotal);
			long refundAvgTime = (long) (refundTotalTime / rTotal);
			long inquiryAvgTime = (long) (inquiryTotalTime / iTotal);

			long time = end - start;

			long throughput = (long) (threadNums[p] * TEST_NUM / time) ;
			System.out.println("ThreadNum: " + threadNums[p]  + '\n' + ">>>"
 					+ "Total Execution Time(ms): " + time + '\n' + ">>>"
 					+ "Throughput(kop/s): " + throughput + '\n' + ">>>"
 					+ "QueryTicket Average Time(ns): " + inquiryAvgTime + '\n' + ">>>"
 					+ "BuyTicket Average Time(ns): " + buyAvgTime + '\n' + ">>>"
 					+ "RefundTicket Average Time(ns): " + refundAvgTime);
//			System.out.println(inquiryTotalTime);
//			System.out.println(iTotal);

			List list = new ArrayList();
			list.add(threadNums[p]);
			list.add(time);
			list.add(throughput);
			list.add(inquiryAvgTime);
			list.add(buyAvgTime);
			list.add(refundAvgTime);
			resultList.add(list);

			flush();
		}
		FileWriter writer = new FileWriter("test.csv");
		csvPrinter = CSVFormat.EXCEL.print(writer);
		Object[] cells = {"ThreadNum", "Total Execution Time(ms)",
				"Throughput(kop/s)", "QueryTicket Average Time(ns)",
				"BuyTicket Average Time(ns)", "RefundTicket Average Time(ns)"};
		csvPrinter.printRecord(cells);
		for (Object o : resultList) {
			List oList = (List) o;
			objects = oList.toArray();
			csvPrinter.printRecord(objects);

		}
		csvPrinter.flush();
		csvPrinter.close();
	}

	private static long addAndGet(long[] array, int threadNums) {
		long res = 0;
		for (int i = 0; i < threadNums; ++i)
			res += array[i];
		return res;
	}

	private static void flush() {
		threadId.set(0);
		long[][] arrays = { buyTicketTime, refundTime, inquiryTime, buyTotal, refundTotal, inquiryTotal };
		for (int i = 0; i < arrays.length; ++i)
			Arrays.fill(arrays[i], 0);
	}
}
