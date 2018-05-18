package cf.coiltech.com.stok.network;

import java.util.List;

import cf.coiltech.com.stok.network.model.Contact;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by ravi on 31/01/18.
 */

public interface ApiService {

    @GET("ImageJsonData.php")
    Single<List<Contact>> getContacts(@Query("source") String source, @Query("search") String query);
}
