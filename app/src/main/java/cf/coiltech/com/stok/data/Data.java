package cf.coiltech.com.stok.data;

/**
 * Created by Kuncoro on 26/03/2016.
 */
public class Data {
    private String id, urunAdi,urunMarka,urunAdet;

    public Data() {
    }

    public Data(String id, String nama, String alamat) {
        this.id = id;
        this.urunAdi = urunAdi;
        this.urunMarka = urunMarka;
        this.urunAdet = urunAdet;
    }

    public String getUrunAdi() {
        return urunAdi;
    }

    public String getUrunMarka() {
        return urunMarka;
    }

    public void setUrunAdi(String urunAdi) {
        this.urunAdi = urunAdi;
    }

    public void setUrunMarka(String urunMarka) {
        this.urunMarka = urunMarka;
    }

    public String getUrunAdet() {
        return urunAdet;
    }

    public void setUrunAdet(String urunAdet) {
        this.urunAdet = urunAdet;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
