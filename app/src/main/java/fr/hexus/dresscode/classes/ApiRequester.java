package fr.hexus.dresscode.classes;

import fr.hexus.dresscode.retrofit.DresscodeService;
import fr.hexus.dresscode.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ApiRequester
{
    private boolean requestSuccedeed;
    private Retrofit retrofit = RetrofitClient.getClient();
    private DresscodeService service = retrofit.create(DresscodeService.class);

    public boolean sendNewWardrobeElement(String token, WardrobeElementForm wardrobeElementForm)
    {
        requestSuccedeed = true;

        Call<Void> call = service.addWardrobeElement(token, wardrobeElementForm);

        call.enqueue(new Callback<Void>()
        {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response)
            {
                requestSuccedeed = (response.errorBody() == null);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t)
            {
                requestSuccedeed = false;
            }
        });

        return requestSuccedeed;
    }
}
