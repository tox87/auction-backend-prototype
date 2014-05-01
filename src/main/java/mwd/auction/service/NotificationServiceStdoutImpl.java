package mwd.auction.service;

import mwd.auction.domain.User;

public class NotificationServiceStdoutImpl implements NotificationService {

    private interface NotificationMessage {
        String OVERBIDDEN = "%s, Your bid was overbidden\n";
        String LESS_THEN_MIN_PRODUCT_PRICE = "%s, Bid rejected! Amount is less then product's min price\n";
        String LESS_THEN_MAX_BID = "%s, Bid rejected! Amount is less then current winning bid\n";
        String WINNING = "%s, You win! Bid amount is greater then product's reserved price\n";
        String BIDDING_ENDED = "%s, Bidding has ended\n";
    }

    @Override
    public void sendOverbiddenNotification(User user) {
        System.out.printf(NotificationMessage.OVERBIDDEN, user.getName());
    }

    @Override
    public void sendLessThenMinProductPriceNotification(User user) {
        System.out.printf(NotificationMessage.LESS_THEN_MIN_PRODUCT_PRICE, user.getName());
    }

    @Override
    public void sendLessThenMaxBidNotification(User user) {
        System.out.printf(NotificationMessage.LESS_THEN_MAX_BID, user.getName());
    }

    @Override
    public void sendWinningNotification(User user) {
        System.out.printf(NotificationMessage.WINNING, user.getName());
    }

    @Override
    public void sendBiddingHasEndedNotification(User user) {
        System.out.printf(NotificationMessage.BIDDING_ENDED, user.getName());
    }

}
