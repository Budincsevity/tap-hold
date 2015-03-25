package com.tep.android.taphold.DAO;

import android.content.Context;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;
import com.tep.android.taphold.beans.Gamer;
import com.tep.android.taphold.utils.AzureProfileManager;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GamerDAO {
    private MobileServiceClient mClient;
    private MobileServiceTable<Gamer> mGamerTable;

    public GamerDAO(Context context) {
        initializeMobileService(context);
        mGamerTable = mClient.getTable(Gamer.class);
    }

    private void initializeMobileService(Context context) {
        String key = AzureProfileManager.getKey();
        String url = AzureProfileManager.getUrl();
        try {
            mClient = new MobileServiceClient(
                    url,
                    key,
                    context);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void insert(Gamer entity) throws ExecutionException, InterruptedException {
        mGamerTable.insert(entity).get();
    }

    public List<Gamer> getTop10Gamers() throws ExecutionException, InterruptedException {
        MobileServiceList<Gamer> gamers = mGamerTable.orderBy("points", QueryOrder.Descending).top(10).execute().get();
        return gamers;
    }
}
