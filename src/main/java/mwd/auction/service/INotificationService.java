/*
 * Created by Mikhail Danilov
 */

package mwd.auction.service;

import mwd.auction.domain.User;

public interface INotificationService {

    void sendOverbiddenNotification(User user);

    void sendLessThenMinProductPriceNotification(User user);

    void sendLessThenMaxBidNotification(User user);

    void sendWinningNotification(User user);

    void sendBiddingHasEndedNotification(User user);

}
