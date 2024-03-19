package com.projects.ecommerce.service;

import com.projects.ecommerce.exception.PurchaseOrderException;
import com.projects.ecommerce.model.Address;
import com.projects.ecommerce.model.PurchaseOrder;
import com.projects.ecommerce.model.User;

import java.util.List;

public interface PurchaseOrderService {
    public PurchaseOrder createOrder(User user, Address shippingAddress);

    public PurchaseOrder findPurchaseOrderById(Long orderId) throws PurchaseOrderException;

    public List<PurchaseOrder> userOrdersHistory(Long userId);

    public PurchaseOrder placedOrder(Long orderId) throws PurchaseOrderException;

    public PurchaseOrder confirmedOrder(Long orderId) throws PurchaseOrderException;

    public PurchaseOrder shippedOrder(Long orderId) throws PurchaseOrderException;

    public PurchaseOrder deliveredOrder(Long orderId) throws PurchaseOrderException;

    PurchaseOrder canceledOrder(Long orderId) throws PurchaseOrderException;

    public List<PurchaseOrder>getAllPurchaseOrders();

    public void deletePurchaseOrder(Long orderId) throws PurchaseOrderException;


}
