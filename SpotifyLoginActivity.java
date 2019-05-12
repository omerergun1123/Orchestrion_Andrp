public class SpotifyLoginActivity extends AppCompatActivity {

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        Log.d("myTag", "Inside onNewIntent 1 ");
        if (uri != null) {
            AuthenticationResponse response = AuthenticationResponse.fromUri(uri);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response
                    LoginActivity.tokenn = response.getAccessToken();
                    ShowPlaylistActivity.token = "Bearer " + response.getAccessToken();
                    Log.d("myTag", "Inside onNewIntent 2" + LoginActivity.tokenn);

                    getUserInfo(LoginActivity.tokenn);
                    final Handler handler=new Handler();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("myTag", "Sem is waiting 1 ");
                            try {
                                semSpotify.acquire();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Log.d("myTag", "Sem is waiting 2 ");

                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    semSpotify.release();
                                    boolean hasAnAccountOnDatabase = false;
									
				     /////////////////////trrrryyyy  
                                    if(load("texts1") != null ){
                                        getUserID();
                                        if(!imageURLonDatabase.equals(imageURL))
                                            updateImageForUser();
                                        Log.d("myTag", "Load is not null");
                                        sessionUser.setTokenSpotify(ShowPlaylistActivity.token);
                                        save(sessionUser);
                                        startActivity(new Intent(SpotifyLoginActivity.this,HomePage.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        overridePendingTransition(0,0);
                                        
                                    }
                                    else{
                                        Log.d("myTag", "Load is null");

                                    
                                        try {
                                            
                                            if (con == null) {
                                                warning = "Please check your internet connection";
                                            } else {

                                                String query = " select * from user where username='" + username + "' and isspotifylogin = '1'  ";
                                                Statement stmt = con.createStatement();
                                                ResultSet rs = stmt.executeQuery(query);

                                                while (rs.next()) {
                                                    Log.d("myTag", "User already in database");
                                                    hasAnAccountOnDatabase = true;
                                                }
                                                if (hasAnAccountOnDatabase == true) {
                                                    warning = "User already in database";
                                                }
                                            }
                                        } catch (Exception ex) {
                                            warning = "Exceptions" + ex;
                                        }

                                        if(hasAnAccountOnDatabase == false){                                          
                                            addUserToDatabase();
                                        }

                                        userID = getUserID();
                                        sessionUser = new User(username,userID,true,imageURL);
                                        sessionUser.setStarCount(userStarCount);
                                        sessionUser.setLatestVoteInMillis(latestVoteInMillis);
                                        sessionUser.setTokenSpotify(ShowPlaylistActivity.token);
                                        save(sessionUser);
                                        startActivity(new Intent(SpotifyLoginActivity.this,HomePage.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        overridePendingTransition(0,0);
                                    }
                                }
                            });

                        }
                    }).start();

                    break;

                // Auth flow returned an error
                case ERROR:
                    startActivity(new Intent(SpotifyLoginActivity.this, LoginActivity.class));
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    startActivity(new Intent(SpotifyLoginActivity.this, LoginActivity.class));
                    // Handle other cases
            }
        }
    }



}
