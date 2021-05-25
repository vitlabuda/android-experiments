package cz.vitlabuda.test.retrofittest;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public final class JSONPlaceHolderAPI {
    public static final class User {
        public static final class Address {
            public static final class Geo {
                @NonNull private final String lat;
                @NonNull private final String lng;

                public Geo(@NonNull String lat, @NonNull String lng) {
                    this.lat = lat;
                    this.lng = lng;
                }

                @NonNull
                public String getLat() {
                    return lat;
                }

                @NonNull
                public String getLng() {
                    return lng;
                }
            }

            @NonNull private final String street;
            @NonNull private final String suite;
            @NonNull private final String city;
            @NonNull private final String zipcode;
            @NonNull private final Geo geo;

            public Address(@NonNull String street, @NonNull String suite, @NonNull String city, @NonNull String zipcode, @NonNull Geo geo) {
                this.street = street;
                this.suite = suite;
                this.city = city;
                this.zipcode = zipcode;
                this.geo = geo;
            }

            @NonNull
            public String getStreet() {
                return street;
            }

            @NonNull
            public String getSuite() {
                return suite;
            }

            @NonNull
            public String getCity() {
                return city;
            }

            @NonNull
            public String getZipcode() {
                return zipcode;
            }

            @NonNull
            public Geo getGeo() {
                return geo;
            }
        }

        public static final class Company {
            @NonNull private final String name;
            @NonNull private final String catchPhrase;
            @NonNull private final String bs;

            public Company(@NonNull String name, @NonNull String catchPhrase, @NonNull String bs) {
                this.name = name;
                this.catchPhrase = catchPhrase;
                this.bs = bs;
            }

            @NonNull
            public String getName() {
                return name;
            }

            @NonNull
            public String getCatchPhrase() {
                return catchPhrase;
            }

            @NonNull
            public String getBs() {
                return bs;
            }
        }

        @NonNull private final Integer id;
        @NonNull private final String name;
        @NonNull private final String username;
        @NonNull private final String email;
        @NonNull private final Address address;
        @NonNull private final String phone;
        @NonNull private final String website;
        @NonNull private final Company company;

        public User(@NonNull Integer id, @NonNull String name, @NonNull String username, @NonNull String email, @NonNull Address address, @NonNull String phone, @NonNull String website, @NonNull Company company) {
            this.id = id;
            this.name = name;
            this.username = username;
            this.email = email;
            this.address = address;
            this.phone = phone;
            this.website = website;
            this.company = company;
        }

        @NonNull
        public Integer getId() {
            return id;
        }

        @NonNull
        public String getName() {
            return name;
        }

        @NonNull
        public String getUsername() {
            return username;
        }

        @NonNull
        public String getEmail() {
            return email;
        }

        @NonNull
        public Address getAddress() {
            return address;
        }

        @NonNull
        public String getPhone() {
            return phone;
        }

        @NonNull
        public String getWebsite() {
            return website;
        }

        @NonNull
        public Company getCompany() {
            return company;
        }
    }

    public interface APIInterface {
        @GET("users")
        Call<List<User>> getAllUsers();

        @GET("users/{id}")
        Call<User> getUser(@Path("id") int id);

        @POST("users")
        Call<User> postUser(@Body User user);

        @DELETE("users/{id}")
        Call<Void> deleteUser(@Path("id") int id);
    }
    
    private static Retrofit generateRetrofitInstance() {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .create();

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build();

        return new Retrofit.Builder()
                .baseUrl(App.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
    }

    public static APIInterface generateAPI() {
        return generateRetrofitInstance().create(APIInterface.class);
    }
}
