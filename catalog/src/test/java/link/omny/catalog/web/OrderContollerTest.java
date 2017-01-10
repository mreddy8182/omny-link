package link.omny.catalog.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.List;

import link.omny.catalog.TestApplication;
import link.omny.catalog.model.CustomFeedbackField;
import link.omny.catalog.model.CustomOrderField;
import link.omny.catalog.model.Feedback;
import link.omny.catalog.model.Order;
import link.omny.catalog.model.OrderItem;
import link.omny.catalog.web.OrderController.FeedbackResource;
import link.omny.catalog.web.OrderController.ShortOrder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author Tim Stephenson
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
@WebAppConfiguration
public class OrderContollerTest {
    private static final CustomOrderField CUSTOM_FIELD_2 = new CustomOrderField("field2", "bar");

    private static final CustomOrderField CUSTOM_FIELD_1 = new CustomOrderField("field1", "foo");

    private static final String FEEDBACK = "5 stars";

    private static final String FEEDBACK_CUSTOM_KEY = "timeliness";

    private static final String FEEDBACK_CUSTOM_VALUE = "Good";

    private static final BigDecimal PRICE = new BigDecimal("9.99");

    private static final String INVOICE_REF = "INV123";

    private static final String TENANT_ID = "omny";

    private static final Long CONTACT_ID = 1l;

    @Autowired
    private OrderController svc;

    @Test
    public void testSimpleOrderLifecycle() {
        Order order = createOrder();
        assertNotNull(order);
        assertNotNull(order.getId());
        assertEquals(PRICE, order.getPrice());
        assertEquals(CONTACT_ID, order.getContactId());
        assertEquals(2, order.getPrice().scale());

        ShortOrder order2 = retrieveOrder();
        assertNotNull(order2);
        assertEquals(2, order2.getCustomFields().size());

        order.setInvoiceRef(INVOICE_REF);
        ShortOrder order3 = updateOrder(order.getId(), order);
        assertNotNull(order3);
        assertEquals(INVOICE_REF, order3.getInvoiceRef());

        Feedback feedback = createFeedback();
        addFeedback(order.getId(), feedback);
        FeedbackResource feedback2 = retrieveFeedback(order.getId());
        assertNotNull(feedback2);
        // h2 database does not do created time default
        // assertNotNull(orderIncFeedback.getFeedback().getCreated());
        assertEquals(FEEDBACK, feedback2.getDescription());
        
        feedback.addCustomField(new CustomFeedbackField(FEEDBACK_CUSTOM_KEY,
                FEEDBACK_CUSTOM_VALUE));
        addFeedback(order.getId(), feedback);
        FeedbackResource feedback3 = retrieveFeedback(order.getId());
        assertNotNull(feedback3);
        assertEquals(1, feedback3.getCustomFields().size());
        assertEquals(FEEDBACK_CUSTOM_KEY, feedback3.getCustomFields().get(0)
                .getName());
        assertEquals(FEEDBACK_CUSTOM_VALUE, feedback3.getCustomFields().get(0)
                .getValue());

        deleteOrder(order.getId());

        ShortOrder order4 = retrieveOrder(order.getId());
        assertEquals("deleted", order4.getStage());
    }

    @Test
    public void testOrderWithItemsLifecycle() {
        Order order = createOrderAndItems();
        assertNotNull(order);
        assertNotNull(order.getId());
        assertEquals(2, order.getOrderItems().size());
        OrderItem orderItem = order.getOrderItems().get(0);
        assertEquals(PRICE, orderItem.getPrice());

        ShortOrder order2 = retrieveOrder();
        assertNotNull(order2);

        order.setInvoiceRef(INVOICE_REF);
        ShortOrder order3 = updateOrder(order.getId(), order);
        assertNotNull(order3);
        assertEquals(INVOICE_REF, order3.getInvoiceRef());

        Feedback feedback = createFeedback();
        addFeedback(order.getId(), feedback);
        addFeedback(order.getId(), feedback);

        deleteOrder(order.getId());

        ShortOrder order4 = retrieveOrder(order.getId());
        assertEquals("deleted", order4.getStage());
    }

    private Order createOrder() {
        Order order = getSimpleOrder();
        svc.create(TENANT_ID, order);

        return order;
    }

    private Order getSimpleOrder() {
        Order order = new Order("1 type A widget");
        order.setPrice(PRICE);
        order.setContactId(CONTACT_ID);

        order.addCustomField(CUSTOM_FIELD_1);
        order.addCustomField(CUSTOM_FIELD_2);

        return order;
    }

    private Order createOrderAndItems() {
        Order order = getOrderAndItems();
        svc.create(TENANT_ID, order);

        return order;
    }

    private Order getOrderAndItems() {
        Order order = new Order("Basket");

        OrderItem orderItem1 = new OrderItem("Widget A", "Widget");
        orderItem1.setPrice(PRICE);
        order.addOrderItem(orderItem1);
        
        OrderItem orderItem2 = new OrderItem("Widget B", "Widget");
        orderItem2.setPrice(PRICE);
        order.addOrderItem(orderItem2);

        return order;
    }

    private ShortOrder retrieveOrder() {
        List<ShortOrder> allOrders = svc.listForTenant(TENANT_ID, null, null);
        assertEquals(1, allOrders.size());
        return allOrders.get(0);
    }

    private ShortOrder retrieveOrder(Long orderId) {
        return svc.readOrder(TENANT_ID, orderId);
    }

    private FeedbackResource retrieveFeedback(Long orderId) {
        return svc.readFeedback(TENANT_ID, orderId);
    }

    private ShortOrder updateOrder(Long orderId, Order updatedOrder) {
        svc.update(TENANT_ID, orderId, updatedOrder);
        ShortOrder retrievedOrder = retrieveOrder();
        return retrievedOrder;
    }

    private Feedback addFeedback(Long orderId, Feedback feedback) {
        svc.addFeedback(TENANT_ID, orderId, feedback);
        return feedback;
    }

    private Feedback createFeedback() {
        Feedback feedback = new Feedback();
        feedback.setDescription(FEEDBACK);
        return feedback;
    }

    private void deleteOrder(Long orderId) {
        svc.delete(TENANT_ID, orderId);
    }
}