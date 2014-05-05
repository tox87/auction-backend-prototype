package mwd.auction;

import mwd.auction.domain.Bid;
import mwd.auction.domain.Product;
import mwd.auction.domain.User;
import mwd.auction.service.BidServiceImpl;
import mwd.auction.service.IBidService;
import mwd.auction.service.INotificationService;
import mwd.auction.service.NotificationServiceStdoutImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class AuctionApp extends TimerTask {

    private int auctionBidLimit;
    private int bidDelayMillis;

    private List<User> users = new ArrayList<>();
    private List<Product> products = new ArrayList<>();
    private Random random = new Random();

    private Timer timer;
    private int bidCounter;

    private IBidService bidService;

    public AuctionApp() {
        initServices();
        addProducts();
        addUsers();
    }

    private void initServices() {
        INotificationService notificationService = new NotificationServiceStdoutImpl();
        bidService = new BidServiceImpl(notificationService);
    }

    public static void main(String[] args) {
        new AuctionApp().startAuction(10, 1000);
    }

    public void startAuction(int auctionBidLimit, int bidDelayMillis) {
        this.auctionBidLimit = auctionBidLimit;
        this.bidDelayMillis = bidDelayMillis;

        printAuctionStartedMessage();
        printAuctionParams();
        resetBidCounter();
        initTimerAndScheduleBiddingTask();
    }

    private void resetBidCounter() {
        bidCounter = 0;
    }

    private void initTimerAndScheduleBiddingTask() {
        timer = new Timer();
        timer.schedule(this, 0, bidDelayMillis);
    }

    @Override
    public void run() {
        Bid bid = buildRandomBid();
        printBid(bid);
        bidService.placeBid(bid);
        if (++bidCounter == auctionBidLimit) {
            stopAuction();
            printSortedBidListPerProduct();
        }
    }

    private void stopAuction() {
        stopScheduledBiddingTask();
        printAuctionStoppedMessage();
    }

    private void printSortedBidListPerProduct() {
        products.stream().forEach( p -> {
            System.out.println("Accepted bids for " + p.getTitle());
            bidService.getProductBidsSortedByPriceDesc(p).stream()
                    .forEach(b -> System.out.println(b.getAmount() + " - " + b.getUser().getName()));
        });
    }

    private void stopScheduledBiddingTask() {
        timer.cancel();
    }

    private void printAuctionStartedMessage() {
        System.out.println("*** Auction started ***");
    }

    private void printAuctionStoppedMessage() {
        System.out.println("*** Auction stopped ***");
    }

    private void printAuctionParams() {
        System.out.printf("*** %d bids will be placed with %d ms delay ***%n", auctionBidLimit, bidDelayMillis);
    }

    private void printBid(Bid bid) {
        System.out.printf("placing new bid #%d from %s on %s amount=%s%n", bid.getId(), bid.getUser().getName(),
                bid.getProduct().getTitle(), bid.getAmount());
    }

    private Bid buildRandomBid() {
        User randomUser = getRandomUser();
        Product randomProduct = getRandomProduct();
        BigDecimal randomBidAmount = getRandomBidAmount(randomProduct);

        Bid bid = new Bid();
        bid.setId(bidCounter);
        bid.setWinning(false);
        bid.setAmount(randomBidAmount);
        bid.setBidTime(LocalDateTime.now());
        bid.setProduct(randomProduct);
        bid.setUser(randomUser);

        return bid;
    }

    private User getRandomUser() {
        int randomUserIndex = random.nextInt(users.size());
        return users.get(randomUserIndex);
    }

    private Product getRandomProduct() {
        int randomProductIndex = random.nextInt(products.size());
        return products.get(randomProductIndex);
    }

    private BigDecimal getRandomBidAmount(Product randomProduct) {
        BigDecimal overbidFactor = new BigDecimal("1.1");
        int bidAmountUpperBound = randomProduct.getReservedPrice().multiply(overbidFactor).intValue();
        return BigDecimal.valueOf(random.nextInt(bidAmountUpperBound));
    }

    private void addUsers() {
        User u1 = new User();
        u1.setId(1);
        u1.setEmail("MrOne@mail.com");
        u1.setName("MrOne");
        u1.setGetOverbidNotifications(true);
        users.add(u1);

        User u2 = new User();
        u2.setId(2);
        u2.setEmail("MrTwo@mail.com");
        u2.setName("MrTwo");
        u2.setGetOverbidNotifications(true);
        users.add(u2);
    }

    private void addProducts() {
        Product p1 = new Product();
        p1.setId(1);
        p1.setTitle("Golden Apple");
        p1.setAuctionEndTime(LocalDateTime.now().plusHours(1));
        p1.setReservedPrice(new BigDecimal("1000"));
        p1.setMinimalPrice(new BigDecimal("100"));
        products.add(p1);

        Product p2 = new Product();
        p2.setId(2);
        p2.setTitle("Red Apple");
        p2.setAuctionEndTime(LocalDateTime.now().plusHours(1));
        p2.setReservedPrice(new BigDecimal("100"));
        p2.setMinimalPrice(new BigDecimal("10"));
        products.add(p2);
    }
}