package eu.jnksoftware.discountfinderandroid.models.discounts;


public class TopDiscount {
    private String shopName;
    private String category;
    private String shortDescription;
    private int finalPrice;
    private String productImageURL;
    private int discountId;
    private double shopLatPos;
    private double shopLogPos;

    public TopDiscount(String shortDescription, String productImage) {
        this.shortDescription = shortDescription;
        this.productImageURL = productImage;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getProductImage() {
        return productImageURL;
    }
}