package mwd.auction.service;

import mwd.auction.domain.Bid;
import mwd.auction.domain.Product;

import java.util.List;


public interface IBidService {

    void placeBid(Bid newBid);

    List<Bid> getProductBidsSortedByPriceDesc(Product product);

}
