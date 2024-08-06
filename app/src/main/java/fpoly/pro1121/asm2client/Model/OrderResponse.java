package fpoly.pro1121.asm2client.Model;

import java.util.ArrayList;

public class OrderResponse {
    private int status;
    private String message;
    private ArrayList<String> data;

    public OrderResponse(int status, String message, ArrayList<String> data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<String> getData() {
        return data;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
    }
}
