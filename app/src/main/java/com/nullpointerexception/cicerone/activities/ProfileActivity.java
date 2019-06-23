package com.nullpointerexception.cicerone.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kinda.mtextfield.ExtendedEditText;
import com.kinda.mtextfield.TextFieldBoxes;
import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.components.AuthenticationManager;
import com.nullpointerexception.cicerone.components.BackEndInterface;
import com.nullpointerexception.cicerone.components.Feedback;
import com.nullpointerexception.cicerone.components.ObjectSharer;
import com.nullpointerexception.cicerone.components.ProfileImageFetcher;
import com.nullpointerexception.cicerone.components.User;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TextView mEmail, mTelephone, mDate, mName, mItinerariesAsParticipant, completedFeedBackMsg_tv, sndFeedBtn, removeFeedback, feedbackTitle, goFeedBacksList;
    private ExtendedEditText mComment;
    private TextFieldBoxes textFieldBoxes;
    private ImageView profileImage;
    private RatingBar ratingBar;
    private float rating;
    private User user;

    private int position = 0;
    private boolean found = false;

    private RecyclerView recyclerView;
    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window w = getWindow();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Initialize UI
        initUI();

        //Change color of toolbar
        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#FF5500"));

        Intent intent = getIntent();
        String extra = intent.getExtras().getString("id_cicerone_to_show");

        user = new User();
        user.setId(extra);

        BackEndInterface.get().getEntity(user, new BackEndInterface.OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                if(user.getId().isEmpty() && user.getDisplayName().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Impossibile caricare le" +
                            "informazioni di profilo", Toast.LENGTH_SHORT);
                    finish();

                }else {
                    //Set text in the field
                    setTextField();
                }
            }

            @Override
            public void onError() {

            }
        });

        goFeedBacksList.setOnClickListener(v -> {
            Intent intent2 = new Intent(this, FeedBacksActivity.class);
            ObjectSharer.get().shareObject("feedback", user);
            startActivity(intent2);
        });
    }

    public void initUI() {
        mEmail = findViewById(R.id.profileMail_tv);
        mTelephone = findViewById(R.id.profilePhone_tv);
        mDate = findViewById(R.id.profileDate_tv);
        mName = findViewById(R.id.profileName_tv);
        mItinerariesAsParticipant = findViewById(R.id.itinerariesAsParticipant_tv);
        profileImage = findViewById(R.id.us_image);
        ratingBar = findViewById(R.id.ratingBar);
        mComment = findViewById(R.id.commento_tv);
        sndFeedBtn = findViewById(R.id.sndFeed_btn);
        constraintLayout = findViewById(R.id.feedback_layout);
        completedFeedBackMsg_tv = findViewById(R.id.completedFeedBackMsg_tv);
        removeFeedback = findViewById(R.id.deleteFeedback_tv);
        feedbackTitle = findViewById(R.id.feedbackTitle_tv);
        goFeedBacksList = findViewById(R.id.goFeedBacksList);
        textFieldBoxes = findViewById(R.id.feedback_box);
    }

    /**
     * @brief Set text field
     */
    public void setTextField() {

        User userLogged = AuthenticationManager.get().getUserLogged();
        Feedback feedback = new Feedback(userLogged);

        new ProfileImageFetcher(this)
                .fetchImageOf(user, drawable -> {
                    profileImage.setImageDrawable(drawable);
                });

        found = false;
        position = 0;

        for(int i = 0; i < user.getFeedbacks().size(); ++i) {
            if(userLogged.getId().equals(user.getFeedbacks().get(i).getIdUser())) {
                mComment.setText(user.getFeedbacks().get(i).getComment());
                ratingBar.setRating((float) user.getFeedbacks().get(i).getVote());
                feedbackTitle.setVisibility(View.GONE);
                sndFeedBtn.setText("Modifica");
                position = i;
                found = true;
            }
        }
        //found is true if the user has already a feedback
        if(!found)
            removeFeedback.setEnabled(false);
        else
            if(mComment.getText().toString().isEmpty())
                mComment.setHint("Non hai lasciato nessun commento nel tuo feedback");

        //Set name field
        mName.setText(user.getDisplayName());

        //set email
        if(user.getEmail() != null) {
            mEmail.setText(user.getEmail());
        }

        //set dateBirth
        if(user.getDateBirth() != null) {
            mDate.setText(user.getDateBirth());
        }

        //Set the number of itinerary in which the user participated
        mItinerariesAsParticipant.setText(String.valueOf(user.getItineraries().size()));

        //Set telephone number
        mTelephone.setText(user.getPhoneNumber());

        //Set ratingBar listener
        ratingBarListener();




        sndFeedBtn.setOnClickListener(v -> {

            if (found) {
                if(checkError()) {
                    feedback.setComment(mComment.getText().toString());

                    if (rating != 0 && rating != user.getFeedbacks().get(position).getVote()) {
                        feedback.setVote((int) rating);
                        user.editFeedback(feedback);

                        BackEndInterface.get().storeEntity(user,
                                new BackEndInterface.OnOperationCompleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        completedFeedBackMsg_tv.setVisibility(View.VISIBLE);
                                        removeFeedback.setEnabled(true);

                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                    } else{
                        Toast.makeText(getApplicationContext(), "Hai inserito lo stesso voto", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                if(mComment != null && !mComment.getText().toString().isEmpty()) {
                    feedback.setComment(mComment.getText().toString());
                }
                if (rating != 0) {
                    feedback.setVote((int) rating);
                    user.addFeedback(feedback);

                    BackEndInterface.get().storeEntity(user,
                            new BackEndInterface.OnOperationCompleteListener() {
                                @Override
                                public void onSuccess() {
                                    completedFeedBackMsg_tv.setVisibility(View.VISIBLE);
                                    removeFeedback.setEnabled(true);
                                }

                                @Override
                                public void onError() {

                                }
                            });
                }
            }
        });

        removeFeedback.setOnClickListener(v -> {
            for(int i = 0; i < user.getFeedbacks().size(); ++i) {
                if(userLogged.getId().equals(user.getFeedbacks().get(i).getIdUser())) {
                    user.getFeedbacks().remove(i);

                    BackEndInterface.get().storeEntity(user,
                            new BackEndInterface.OnOperationCompleteListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getApplicationContext(), "Hai rimosso " +
                                    "il feedback", Toast.LENGTH_SHORT).show();
                            removeFeedback.setEnabled(false);
                        }

                        @Override
                        public void onError() {

                        }
                    });

                    break;
                }
            }
        });

        recyclerView = findViewById(R.id.RecyclerView_Review);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        AdapterReview adapter = new AdapterReview(getApplicationContext(), user.getFeedbacks());
        recyclerView.setAdapter(adapter);
    }

    private boolean checkError() {
        boolean alright = true;

        if (mComment != null &&
                mComment.getText().toString().equals(user.getFeedbacks().get(position).getComment())
                && !user.getFeedbacks().get(position).getComment().isEmpty()
                && !mComment.getText().toString().isEmpty()) {
            textFieldBoxes.setError("Hai inserito lo stesso commento", true);
            alright = false;
        }

        return alright;
    }
    private void ratingBarListener() {

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            this.rating = rating;
        });

    }


}

class AdapterReview extends RecyclerView.Adapter <AdapterReview.MyViewHolder>
{
    private Context context;
    private List<Feedback> feedbacks;
    private LayoutInflater inflater;

    public AdapterReview(Context appContext,List<Feedback> feedbacks)
    {
        this.context = appContext;
        this.feedbacks = feedbacks;
        inflater = (LayoutInflater.from(appContext));


    }

    @NonNull
    @Override
    public AdapterReview.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = inflater.inflate(R.layout.review_layout, parent, false);
        AdapterReview.MyViewHolder vh = new AdapterReview.MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterReview.MyViewHolder holder, int position) {
        holder.displayName.setText(feedbacks.get(position).getDisplayNameUser());
        holder.description.setText(feedbacks.get(position).getComment());
        holder.ratingBar.setRating((float) feedbacks.get(position).getVote());

        User user = new User();
        user.setId(feedbacks.get(position).getIdUser());
        user.setProfileImageUrl(feedbacks.get(position).getProfileImageUrlUser());

        new ProfileImageFetcher(context).fetchImageOf(user, drawable ->
        {

            if(drawable != null)
                holder.imgProfile.setImageDrawable(drawable);
        });


        //Ascolto l'evento click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }

    @Override
    public int getItemCount() {

        if(feedbacks.size()>2)
            return 2;
        else
            return feedbacks.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView displayName, description;
        ImageView imgProfile;
        RatingBar ratingBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            displayName = (TextView) itemView.findViewById(R.id.textView_DisplayName);
            description = (TextView) itemView.findViewById(R.id.textView_Description);
            imgProfile = (ImageView) itemView.findViewById(R.id.imageView_ProfileImage);
            ratingBar = itemView.findViewById(R.id.ratingBar2);

        }
    }
}
