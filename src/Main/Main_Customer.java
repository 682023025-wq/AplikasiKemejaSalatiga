/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Main;

import CustomerView.CustomerMenuLogin;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
/**
 *
 * @author AGIL
 */

public class Main_Customer {
    public static void main(String[] args) {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        CustomerMenuLogin Login = new CustomerMenuLogin();
        Login.show();
    }
}