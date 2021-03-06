package com.nullpointerexception.cicerone.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.components.AuthenticationManager;
import com.nullpointerexception.cicerone.components.Blocker;
import com.nullpointerexception.cicerone.components.Feedback;
import com.nullpointerexception.cicerone.components.ObjectSharer;
import com.nullpointerexception.cicerone.components.ProfileImageFetcher;
import com.nullpointerexception.cicerone.components.User;

import java.util.ArrayList;
import java.util.List;

/**
 * FeedbacksActivity
 *
 * Show the list of feedback.
 * Used class = {@link Feedback} {@link Blocker} {@link AuthenticationManager} {@link ObjectSharer} {@link ProfileImageFetcher} {@link User}
 * @author Mattia
 */
public class FeedBacksActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_backs);


        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#FF5500"));

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_FeedBacksActivity);
        setSupportActionBar(toolbar);

        //Check if actionbar is initialized
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<Feedback> feedbacks = new ArrayList<Feedback>();

        if(ObjectSharer.get().getSharedObject("feedback") != null)
        {
            User user = (User) ObjectSharer.get().getSharedObject("feedback");
            String IdUserLogged = AuthenticationManager.get().getUserLogged().getId();
            for(int i=0; i<user.getFeedbacks().size(); i++)
            {
                if(!IdUserLogged.equals(user.getFeedbacks().get(i).getIdUser())){
                    feedbacks.add(user.getFeedbacks().get(i));
                }
            }

        }else {
            feedbacks = AuthenticationManager.get().getUserLogged().getFeedbacks();
        }


        RecyclerView recyclerView = findViewById(R.id.RecyclerView_Review);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        AdapterReviewFeedBack adapter = new AdapterReviewFeedBack(getApplicationContext(), feedbacks);
        recyclerView.setAdapter(adapter);


    }

    /**
     * Catch the click on back arrow and remove object on objectSherer
     */
    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ObjectSharer.get().remove("feedback");
    }


}

class AdapterReviewFeedBack extends RecyclerView.Adapter <AdapterReviewFeedBack.MyViewHolder>
{
    private Context context;
    private List<Feedback> feedbacks;
    private LayoutInflater inflater;

    AdapterReviewFeedBack(Context appContext, List<Feedback> feedbacks)
    {
        this.context = appContext;
        this.feedbacks = feedbacks;
        inflater = (LayoutInflater.from(appContext));
    }

    @NonNull
    @Override
    public AdapterReviewFeedBack.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = inflater.inflate(R.layout.review_layout, parent, false);
        AdapterReviewFeedBack.MyViewHolder vh = new AdapterReviewFeedBack.MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterReviewFeedBack.MyViewHolder holder, int position) {

        holder.displayName.setText(feedbacks.get(position).getDisplayNameUser());
        holder.description.setText(feedbacks.get(position).getComment());
        holder.ratingBar.setRating((float) feedbacks.get(position).getVote());

        User user = new User();
        user.setId(feedbacks.get(position).getIdUser());
        String displayName = feedbacks.get(position).getDisplayNameUser();
        if(displayName.contains(" "))
        {
            user.setName(displayName.substring(0, displayName.indexOf(" ")));
            user.setSurname(displayName.substring(displayName.indexOf(" ")+1));
        }
        else
            user.setName(displayName);
        user.setProfileImageUrl(feedbacks.get(position).getProfileImageUrlUser());

        new ProfileImageFetcher(context).fetchImageOf(user, drawable ->
        {
            if(drawable != null)
                holder.imgProfile.setImageDrawable(drawable);
        });

        //Ascolto l'evento click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            private Blocker mBlocker = new Blocker();

            @Override
            public void onClick(View v) {
                if (!mBlocker.block()) {
                    Intent intent2 = new Intent(v.getContext(), ProfileActivity.class);
                    intent2.putExtra("id_cicerone_to_show",feedbacks.get(position).getIdUser());
                    intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(intent2);
                }
            }
        });
    }

    @Override
    public int getItemCount() {

        return feedbacks.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView displayName, description;
        ImageView imgProfile;
        RatingBar ratingBar;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            displayName = itemView.findViewById(R.id.textView_DisplayName);
            description = itemView.findViewById(R.id.textView_Description);
            imgProfile = itemView.findViewById(R.id.imageView_ProfileImage);
            ratingBar = itemView.findViewById(R.id.ratingBar2);

        }
    }
}

