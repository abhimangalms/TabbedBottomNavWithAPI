package info.project.orion.fragment.home;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import info.project.orion.Activity.DetailViewActivity;
import info.project.orion.Model.Events;
import info.project.orion.R;
import info.project.orion.url.Url;
import info.project.orion.app.MyApplication;
import info.project.orion.fragment.placements.CompanyFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = CompanyFragment.class.getSimpleName();

    // url to fetch event items
    private static final String whatsNewURL = Url.sportsUrl;;
    private static final String eventsURL = Url.techFestUrl;;
    private static final String workshopsURL = Url.workshopsUrl;;

    private RecyclerView mWhatsNewRecyclerView;
    private RecyclerView mEventsRecyclerView;
    private RecyclerView mWorkshopsRecyclerView;
    private List<Events> itemsList;
    private HomeFragment.StoreAdapter mAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static CompanyFragment newInstance(String param1, String param2) {
        CompanyFragment fragment = new CompanyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mWhatsNewRecyclerView = view.findViewById(R.id.whatsNewRecyclerView);
        mEventsRecyclerView = view.findViewById(R.id.eventsRecyclerView);
        mWorkshopsRecyclerView = view.findViewById(R.id.workshopsRecyclerView);

        itemsList = new ArrayList<>();
        mAdapter = new HomeFragment.StoreAdapter(getActivity(), itemsList);

//        RecyclerView.LayoutManager mLayoutManagerWhatsNew = new GridLayoutManager(getActivity(), itemsList.size());
        RecyclerView.LayoutManager mLayoutManagerWhatsNew = new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false);
        RecyclerView.LayoutManager mLayoutManagerEvents = new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false);
        RecyclerView.LayoutManager mLayoutManagerWorkshops = new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false);

//      for whatsNew RecyclerView
        mWhatsNewRecyclerView.setLayoutManager(mLayoutManagerWhatsNew);
        mWhatsNewRecyclerView.addItemDecoration(new HomeFragment.GridSpacingItemDecoration(2, dpToPx(8), true));
        mWhatsNewRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mWhatsNewRecyclerView.setAdapter(mAdapter);
        mWhatsNewRecyclerView.setNestedScrollingEnabled(false);

//        for Events RecyclerView
        mEventsRecyclerView.setLayoutManager(mLayoutManagerEvents);
        mEventsRecyclerView.addItemDecoration(new HomeFragment.GridSpacingItemDecoration(2, dpToPx(8), true));
        mEventsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mEventsRecyclerView.setAdapter(mAdapter);
        mEventsRecyclerView.setNestedScrollingEnabled(false);

//      for Workshops RecyclerView
        mWorkshopsRecyclerView.setLayoutManager(mLayoutManagerWorkshops);
        mWorkshopsRecyclerView.addItemDecoration(new HomeFragment.GridSpacingItemDecoration(2, dpToPx(8), true));
        mWorkshopsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mWorkshopsRecyclerView.setAdapter(mAdapter);
        mWorkshopsRecyclerView.setNestedScrollingEnabled(false);



        fetchRecyclerViewItems(whatsNewURL);
        fetchRecyclerViewItems(eventsURL);
        fetchRecyclerViewItems(workshopsURL);
        fetchRecyclerViewItems(workshopsURL);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

    /**
     * fetching shopping item by making http call
     */
    private void fetchRecyclerViewItems(String URL) {
        JsonArrayRequest request = new JsonArrayRequest(URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(getActivity(), "Couldn't fetch the store items! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<Events> items = new Gson().fromJson(response.toString(), new TypeToken<List<Events>>() {
                        }.getType());

                        itemsList.clear();
                        itemsList.addAll(items);

                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        MyApplication.getInstance().addToRequestQueue(request);
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    /**
     * RecyclerView adapter class to render items
     * This class can go into another separate class, but for simplicity
     */
    class StoreAdapter extends RecyclerView.Adapter<HomeFragment.StoreAdapter.MyViewHolder> {
        private Context context;
        private List<Events> movieList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView titleTextView, dateTextView;
            public ImageView imageView;

            public MyViewHolder(View view) {
                super(view);
                titleTextView = view.findViewById(R.id.location);
                dateTextView = view.findViewById(R.id.date);
                imageView = view.findViewById(R.id.thumbnail);
            }
        }


        public StoreAdapter(Context context, List<Events> movieList) {
            this.context = context;
            this.movieList = movieList;
        }

        @Override
        public HomeFragment.StoreAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.home_item_row, parent, false);

            return new HomeFragment.StoreAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(HomeFragment.StoreAdapter.MyViewHolder holder, final int position) {
            final Events events = movieList.get(position);

            holder.titleTextView.setText(events.getTitle());
            holder.dateTextView.setText(events.getDate());
            Glide.with(context)
                    .load(events.getImage())
                    .into(holder.imageView);

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Loading", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, DetailViewActivity.class);
                    intent.putExtra("TITLE", events.getTitle());
                    intent.putExtra("DATE", events.getDate());
                    intent.putExtra("URL", events.getImage());
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return movieList.size();
        }
    }
}