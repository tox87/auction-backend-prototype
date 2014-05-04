package mwd.auction.service;

import mwd.auction.domain.Bid;
import mwd.auction.domain.Product;
import mwd.auction.domain.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BidServiceImpl implements IBidService {

    private List<Bid> bids = new ArrayList<>();

    private INotificationService INotificationService;

    public BidServiceImpl(INotificationService INotificationService) {
        this.INotificationService = INotificationService;
    }

    @Override
    public void placeBid(Bid newBid) {

        if (newBid.getProduct().getAuctionEndTime().isBefore(newBid.getBidTime())) {
            INotificationService.sendBiddingHasEndedNotification(newBid.getUser());
            return;
        }

        if (newBid.getProduct().getMinimalPrice().compareTo(newBid.getAmount()) > 0) {
            INotificationService.sendLessThenMinProductPriceNotification(newBid.getUser());
            return;
        }

        if (newBid.getProduct().getReservedPrice().compareTo(newBid.getAmount()) < 0) {
            INotificationService.sendWinningNotification(newBid.getUser());
            /* product sold out for reservedPrice, so end product bidding with time of winning bid,
            so that all subsequent bids will be rejected by auctionEndTime condition */
            newBid.getProduct().setAuctionEndTime(newBid.getBidTime());
            return;
        }

        List<Bid> productBids = getProductBids(newBid.getProduct());

        Bid winningBid = getWinningBid(productBids);

        if (winningBid != null) {
            if (winningBid.getAmount().compareTo(newBid.getAmount()) >= 0) {
                    INotificationService.sendLessThenMaxBidNotification(newBid.getUser());
                    return;
            }
            //current bid is no longer winning, since new bid amount is higher
            winningBid.setWinning(false);
        }

        newBid.setWinning(true);

        notifyOverbidden(productBids);

        bids.add(newBid);
    }

    @Override
    public List<Bid> getProductBidsSortedByPriceDesc(Product product) {
        Comparator<Bid> byPriceDesc = Comparator.comparing(Bid::getAmount).reversed();
        return getProductBids(product).stream().sorted(byPriceDesc).collect(Collectors.toList());
    }

    private List<Bid> getProductBids(Product product) {
        return bids.stream().filter(bid -> bid.getProduct().equals(product)).collect(Collectors.toList());
    }

    private static Bid getWinningBid(List<Bid> bids) {
        return bids.stream().filter(Bid::isWinning).findFirst().orElse(null);
    }

    private void notifyOverbidden(List<Bid> bids) {
        bids.stream()
                .map(Bid::getUser)
                .filter(User::isGetOverbidNotifications)
                .distinct()
                .forEach(INotificationService::sendOverbiddenNotification);
    }
}
