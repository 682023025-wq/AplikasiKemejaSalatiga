package CustomerModel;

import CustomerView.CustomerHomeCustomer;

public class CustomerModelLink {
    private static CustomerHomeCustomer homeCustomer;

    public static void setHomeCustomer(CustomerHomeCustomer home) {
        homeCustomer = home;
    }

    public static CustomerHomeCustomer getHomeCustomer() {
        return homeCustomer;
    }
}
