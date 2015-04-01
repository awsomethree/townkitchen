package awsomethree.com.townkitchen.helpers;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import awsomethree.com.townkitchen.models.DailyMenu;
import awsomethree.com.townkitchen.models.FoodMenu;
import awsomethree.com.townkitchen.models.Order;
import awsomethree.com.townkitchen.models.OrderLineItem;

/**
 * Created by ktruong on 3/29/15.
 */
public class OrderUtils {

    public static List<OrderLineItem> getStuffOrder() {
        Order order = new Order();
//        DateTime shipDate = DateTime.now().plusDays(2);
        Date shipDate = new Date();
        order.setShipDate(shipDate);

        List items = new LinkedList<>();
        OrderLineItem item1 = new OrderLineItem();

        String imgUrl = "http://static1.squarespace.com/static/54b5bb0de4b0a14bf3e854e0/54b6d4f5e4b0b6737de70fb4/5510937de4b084049ce0f5c3/1427149706700/Egg+Salad+Salmon+Cutting+Board.jpg?format=500w";
        String optionName = "Option #1";
        String optionDesc = "Buffalo Chicken BLT w/ Spiced Macaroni Salad";
        double price = Double.parseDouble("13.00");
        item1.setMenu(newFoodMenu(imgUrl, optionName, optionDesc, price));
        item1.setOrder(order);
        item1.setQty(1);
        item1.setPrice(price);
        items.add(item1);

        OrderLineItem item2 = new OrderLineItem();
        item2.setOrder(order);
        item2.setQty(1);
        item2.setMenu(newFoodMenu(imgUrl, "Option #2", "Smoked Trout Farmers' Salad w/ Asparagus, Roasted Potatoes, Tomato, Cucumber & Meyer Lemon Yogurt Dressing", 13.00d));
        items.add(item2);

        OrderLineItem item3 = new OrderLineItem();
        item3.setOrder(order);
        item3.setQty(1);
        item3.setPrice(price);
        item3.setMenu(newFoodMenu(imgUrl, "Option #3", "Roasted Mushroom Goat Cheese Flatbread w/ Romesco, Sprouts, Cucumber & Baby Arugula Salad in Honey Balsamic", 13.00d));
        items.add(item3);

        OrderLineItem item4 = new OrderLineItem();
        item4.setOrder(order);
        item4.setQty(1);
        item4.setPrice(price);
        item4.setMenu(newFoodMenu(imgUrl, "Option #4", "Smoked Trout Farmers' Salad w/ Asparagus, Roasted Potatoes, Tomato, Cucumber & Meyer Lemon Yogurt Dressing", 13.00d));
        items.add(item4);

        return items;
    }

    public static DailyMenu newFoodMenu(String imgrUrl, String optionName, String optionDesc, double price) {
        DailyMenu dailyMenu = new DailyMenu();
        FoodMenu foodMenu = new FoodMenu();
        foodMenu.setImageUrl(imgrUrl);
        foodMenu.setName(optionName);
        foodMenu.setDescription(optionDesc);
        foodMenu.setPrice(price);
        dailyMenu.setFoodMenu(foodMenu);
        return dailyMenu;
    }
}
