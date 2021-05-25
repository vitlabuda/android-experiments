package cz.vitlabuda.test.retrofittest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.main_text_view);

        //showAllUsers();
        //showSpecificUser(2);
        //createNewUser();
        deleteSpecificUser(3);
    }

    private void showAllUsers() {
        JSONPlaceHolderAPI.APIInterface api = JSONPlaceHolderAPI.generateAPI();

        Call<List<JSONPlaceHolderAPI.User>> call = api.getAllUsers();

        call.enqueue(new Callback<List<JSONPlaceHolderAPI.User>>() {
            @Override
            public void onResponse(Call<List<JSONPlaceHolderAPI.User>> call, Response<List<JSONPlaceHolderAPI.User>> response) {
                if(!response.isSuccessful()) {
                    textView.setText("API request failed with HTTP error code: " + response.code());
                    return;
                }

                List<JSONPlaceHolderAPI.User> responseBody = response.body();
                if(responseBody == null) {
                    textView.setText("The request was successful, but the response body is empty!");
                    return;
                }


                StringBuilder stringBuilder = new StringBuilder();

                for(JSONPlaceHolderAPI.User user : responseBody) {
                    stringBuilder.append("id: ").append(user.getId()).append("\n");
                    stringBuilder.append("name: ").append(user.getName()).append("\n");
                    stringBuilder.append("username: ").append(user.getUsername()).append("\n");
                    stringBuilder.append("email: ").append(user.getEmail()).append("\n");

                    stringBuilder.append("address: ").append("\n");
                    stringBuilder.append("- street: ").append(user.getAddress().getStreet()).append("\n");
                    stringBuilder.append("- suite: ").append(user.getAddress().getSuite()).append("\n");
                    stringBuilder.append("- city: ").append(user.getAddress().getCity()).append("\n");
                    stringBuilder.append("- zipcode: ").append(user.getAddress().getZipcode()).append("\n");

                    stringBuilder.append("- geo: ").append("\n");
                    stringBuilder.append("  - lat: ").append(user.getAddress().getGeo().getLat()).append("\n");
                    stringBuilder.append("  - lng: ").append(user.getAddress().getGeo().getLng()).append("\n");

                    stringBuilder.append("phone: ").append(user.getPhone()).append("\n");
                    stringBuilder.append("website: ").append(user.getWebsite()).append("\n");

                    stringBuilder.append("company: ").append("\n");
                    stringBuilder.append("- name: ").append(user.getCompany().getName()).append("\n");
                    stringBuilder.append("- catchPhrase: ").append(user.getCompany().getCatchPhrase()).append("\n");
                    stringBuilder.append("- bs: ").append(user.getCompany().getBs()).append("\n");

                    stringBuilder.append("\n\n");
                }

                textView.setText(stringBuilder.toString());
            }

            @Override
            public void onFailure(Call<List<JSONPlaceHolderAPI.User>> call, Throwable t) {
                textView.setText("API request threw an exception or an error: " + t.getMessage());
            }
        });
    }

    private void showSpecificUser(int id) {
        JSONPlaceHolderAPI.APIInterface api = JSONPlaceHolderAPI.generateAPI();

        Call<JSONPlaceHolderAPI.User> call = api.getUser(id);

        call.enqueue(new Callback<JSONPlaceHolderAPI.User>() {
            @Override
            public void onResponse(Call<JSONPlaceHolderAPI.User> call, Response<JSONPlaceHolderAPI.User> response) {
                if(!response.isSuccessful()) {
                    textView.setText("API request failed with HTTP error code: " + response.code());
                    return;
                }

                JSONPlaceHolderAPI.User user = response.body();
                if(user == null) {
                    textView.setText("The request was successful, but the response body is empty!");
                    return;
                }


                textView.setText("id: " + user.getId() + "\n" +
                        "name: " + user.getName() + "\n" +
                        "username: " + user.getUsername() + "\n" +
                        "email: " + user.getEmail() + "\n" +
                        "address: " + "\n" +
                        "- street: " + user.getAddress().getStreet() + "\n" +
                        "- suite: " + user.getAddress().getSuite() + "\n" +
                        "- city: " + user.getAddress().getCity() + "\n" +
                        "- zipcode: " + user.getAddress().getZipcode() + "\n" +
                        "- geo: " + "\n" +
                        "  - lat: " + user.getAddress().getGeo().getLat() + "\n" +
                        "  - lng: " + user.getAddress().getGeo().getLng() + "\n" +
                        "phone: " + user.getPhone() + "\n" +
                        "website: " + user.getWebsite() + "\n" +
                        "company: " + "\n" +
                        "- name: " + user.getCompany().getName() + "\n" +
                        "- catchPhrase: " + user.getCompany().getCatchPhrase() + "\n" +
                        "- bs: " + user.getCompany().getBs() + "\n" +
                        "\n\n");
            }

            @Override
            public void onFailure(Call<JSONPlaceHolderAPI.User> call, Throwable t) {
                textView.setText("API request threw an exception or an error: " + t.getMessage());
            }
        });
    }

    private void createNewUser() {
        JSONPlaceHolderAPI.User user = new JSONPlaceHolderAPI.User(
                -1, "John Doe", "johndoe", "john@doe.org",
                new JSONPlaceHolderAPI.User.Address("Test Street", "Suite 123", "Test City", "12345-6789", new JSONPlaceHolderAPI.User.Address.Geo("12.3456", "78.9123")),
                "123-4567-890", "doe.org",
                new JSONPlaceHolderAPI.User.Company("Test Company", "We test this company", "CEO")
        );

        JSONPlaceHolderAPI.APIInterface api = JSONPlaceHolderAPI.generateAPI();
        Call<JSONPlaceHolderAPI.User> call = api.postUser(user);

        call.enqueue(new Callback<JSONPlaceHolderAPI.User>() {
            @Override
            public void onResponse(Call<JSONPlaceHolderAPI.User> call, Response<JSONPlaceHolderAPI.User> response) {
                if(!response.isSuccessful()) {
                    textView.setText("API request failed with HTTP error code: " + response.code());
                    return;
                }

                JSONPlaceHolderAPI.User user = response.body();
                if(user == null) {
                    textView.setText("The request was successful, but the response body is empty!");
                    return;
                }


                textView.setText("id: " + user.getId() + "\n" +
                        "name: " + user.getName() + "\n" +
                        "username: " + user.getUsername() + "\n" +
                        "email: " + user.getEmail() + "\n" +
                        "address: " + "\n" +
                        "- street: " + user.getAddress().getStreet() + "\n" +
                        "- suite: " + user.getAddress().getSuite() + "\n" +
                        "- city: " + user.getAddress().getCity() + "\n" +
                        "- zipcode: " + user.getAddress().getZipcode() + "\n" +
                        "- geo: " + "\n" +
                        "  - lat: " + user.getAddress().getGeo().getLat() + "\n" +
                        "  - lng: " + user.getAddress().getGeo().getLng() + "\n" +
                        "phone: " + user.getPhone() + "\n" +
                        "website: " + user.getWebsite() + "\n" +
                        "company: " + "\n" +
                        "- name: " + user.getCompany().getName() + "\n" +
                        "- catchPhrase: " + user.getCompany().getCatchPhrase() + "\n" +
                        "- bs: " + user.getCompany().getBs() + "\n" +
                        "\n\n");
            }

            @Override
            public void onFailure(Call<JSONPlaceHolderAPI.User> call, Throwable t) {
                textView.setText("API request threw an exception or an error: " + t.getMessage());
            }
        });
    }

    private void deleteSpecificUser(int id) {
        JSONPlaceHolderAPI.APIInterface api = JSONPlaceHolderAPI.generateAPI();
        Call<Void> call = api.deleteUser(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(!response.isSuccessful()) {
                    textView.setText("API request failed with HTTP error code: " + response.code());
                    return;
                }

                textView.setText("The user with ID " + id + " was deleted! (HTTP response code: " + response.code() + ")");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                textView.setText("API request threw an exception or an error: " + t.getMessage());
            }
        });
    }
}