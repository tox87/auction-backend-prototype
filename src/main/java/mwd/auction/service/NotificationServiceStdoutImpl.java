package mwd.auction.service;

import mwd.auction.domain.User;

public class NotificationServiceStdoutImpl implements INotificationService {

    @Override
    public void sendOverbiddenNotification(User user) {
        System.out.printf("%s, Your bid was overbidden\n", user.getName());
    }

    @Override
    public void sendLessThenMinProductPriceNotification(User user) {
        System.out.printf("%s, Bid rejected! Amount is less then product's min price\n", user.getName());
    }

    @Override
    public void sendLessThenMaxBidNotification(User user) {
        System.out.printf("%s, Bid rejected! Amount is less then current winning bid\n", user.getName());
    }

    @Override
    public void sendWinningNotification(User user) {
        System.out.printf("%s, You win! Bid amount is greater then product's reserved price\n", user.getName());
    }

    @Override
    public void sendBiddingHasEndedNotification(User user) {
        System.out.printf("%s, Bidding has ended\n", user.getName());
    }
}
