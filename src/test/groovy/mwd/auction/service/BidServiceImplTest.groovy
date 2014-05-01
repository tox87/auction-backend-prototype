package mwd.auction.service

import mwd.auction.domain.Bid
import mwd.auction.domain.Product
import mwd.auction.domain.User
import spock.lang.Specification

import java.time.LocalDateTime

/**
 * Created by Tox on 27.04.2014.
 */
class BidServiceImplTest extends Specification {

    NotificationService notificationService
    BidServiceImpl bidService

    def setup() {
        notificationService = Mock()
        bidService = new BidServiceImpl(notificationService)
    }

    def "should send notification if bidding on product has ended"() {

        given: "product"
        Product product = new Product()
        product.auctionEndTime = LocalDateTime.now()

        and: "user"
        User user = new User()
        user.name = "John Doe"

        and: "a bid with bidTime after then product's auctionEndTime"
        Bid bid = new Bid()
        bid.product = product
        bid.user = user
        bid.bidTime = product.auctionEndTime.plusDays(1)

        when: "a new bid is placed"
        bidService.placeBid(bid)

        then: "notification that bidding has ended should be sent"
        1 * notificationService.sendBiddingHasEndedNotification(user)

    }

    def "should send notification if amount is less then min product price"() {

        given: "product"
        Product product = new Product()
        product.auctionEndTime = LocalDateTime.now()
        product.minimalPrice = 100

        and: "user"
        User user = new User()
        user.name = "John Doe"

        and: "a bid with amount less then product min price"
        Bid bid = new Bid()
        bid.product = product
        bid.user = user
        bid.bidTime = product.auctionEndTime.minusDays(1)
        bid.amount = product.minimalPrice - 1

        when: "a new bid is placed"
        bidService.placeBid(bid)

        then: "notification that amount is less then min price should be sent"
        1 * notificationService.sendLessThenMinProductPriceNotification(user)
    }

    def "should send a notification if amount is more then reserved product price"() {

        given: "product"
        Product product = new Product()
        product.auctionEndTime = LocalDateTime.now()
        product.minimalPrice = 100
        product.reservedPrice = 200

        and: "user"
        User user = new User()
        user.name = "John Doe"

        and: "a bid with amount more then product reserved price"
        Bid bid = new Bid()
        bid.product = product
        bid.user = user
        bid.bidTime = product.auctionEndTime.minusDays(1)
        bid.amount = product.reservedPrice + 10

        when: "a new bid is placed"
        bidService.placeBid(bid)

        then: "notification that the placed bid has won should be sent"
        1 * notificationService.sendWinningNotification(user)

        and: "auctionEndTime should be set equal to winning bid time to stop further bidding"
        bid.getProduct().getAuctionEndTime() == bid.getBidTime()

    }

    def "should send a notification if new bid amount is less then current max bid amount"() {

        given: "product"
        Product product = new Product()
        product.auctionEndTime = LocalDateTime.now()
        product.minimalPrice = 100
        product.reservedPrice = 200

        and: "user"
        User winningUser = new User()
        winningUser.name = "Superman"

        and: "another user"
        User userPlacingLastBid = new User()
        userPlacingLastBid.name = "Batman"

        and: "current winning bid"
        Bid winningBid = new Bid()
        winningBid.product = product
        winningBid.user = winningUser
        winningBid.bidTime = product.auctionEndTime.minusDays(1)
        winningBid.amount = 150

        and: "new bid with amount less then winning"
        Bid newBid = new Bid()
        newBid.product = product
        newBid.user = userPlacingLastBid
        newBid.bidTime = product.auctionEndTime.minusDays(1)
        newBid.amount = winningBid.amount - 10

        when: "one user places a bid and it becomes winning"
        bidService.placeBid(winningBid)

        and: "another user later places a bid with amount less then winning"
        bidService.placeBid(newBid)

        then: "notification that the last placed bid amount is less then current winning should be sent"
        1 * notificationService.sendLessThenMaxBidNotification(userPlacingLastBid);
        and: "first placed bid should stay winning"
        winningBid.isWinning()
        and: "last placed bid should not be winning"
        !newBid.isWinning()
    }
}
