package javaproject1.DAL.Repo.abstraction;

import java.util.List;
import javaproject1.DAL.Entity.Delivery;

public interface IDeliveryRepo {
    
    public void addDelivery(Delivery delivery);
    public Delivery getDeliveryById(int id);
    public void updateDelivery(Delivery delivery);
    public void deleteDelivery(int id);
    List<Delivery> getAllDeliveries();
    
}