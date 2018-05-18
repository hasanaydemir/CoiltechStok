package cf.coiltech.com.stok.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ravi on 31/01/18.
 */

public class Contact {
    String model;

    @SerializedName("kucuk_resim")

    String kucukResim;

    String marka;
    String stokAdet,urunID;

    public String getModel() {
        return model;
    }

    public String getKucukResim() {
        return kucukResim;
    }

    public String getMarka() {
        return marka;
    }
    public String getID() {

        return urunID;

    }

    public String getStokAdet() {
        return stokAdet;
    }

    /**
     * Checking contact equality against email
     */
    @Override
    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof Contact)) {
            return ((Contact) obj).getStokAdet().equalsIgnoreCase(stokAdet);
        }
        return false;
    }
}
