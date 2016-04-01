package com.hdfc.libs;

import android.content.Context;
import android.os.Handler;

import com.hdfc.config.Config;
import com.scottyab.aescrypt.AESCrypt;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CacheManager;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.App42Response;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;
import com.shephertz.app42.paas.sdk.android.upload.Upload;
import com.shephertz.app42.paas.sdk.android.upload.UploadFileType;
import com.shephertz.app42.paas.sdk.android.upload.UploadService;
import com.shephertz.app42.paas.sdk.android.user.User;
import com.shephertz.app42.paas.sdk.android.user.UserService;

import org.json.JSONObject;

import java.security.GeneralSecurityException;
import java.util.ArrayList;

public class AsyncApp42ServiceApi {

    private static AsyncApp42ServiceApi mInstance = null;
    private static String apiKey = "";
    private static String apiSecret = "";
    private UserService userService;
    private StorageService storageService;
    private UploadService uploadService;


    private AsyncApp42ServiceApi(Context context) {

        try {
            if (Config.release) {//for release
                apiKey = AESCrypt.decrypt(Config.string, "gybi3gsBYxnuYlyOlEExwGiyLd0um74K2fVLphtFpt3GkTBoxvew3lPxrVtBpFVJhdJDhHg3wfXR3HmshlC5XBmMM50dEWXZ7/Z8TJ78wt4=");
                apiSecret = AESCrypt.decrypt(Config.string, "tdSH3TJGp/KiMIVp9CZOTi5Rdw+x1xADUQiMzdWIf8gbI5V+VlQACV3IIE4EMfqNy9qv1YW4tMJyq90xP0tKa99pmIDzNVbIZ3eiVOeWG+4=");
            } else {
                apiKey = AESCrypt.decrypt(Config.string, "a3tEVMM63P40VdMvybYAHvhjdR91k6uRHnBoRIjQq7bEYH0jWh22DnT6eYLCPv+3X0UrhjF4nwresW4BA1bKBXvUIl2/Z2cqfdtY5la00U4=");
                apiSecret = AESCrypt.decrypt(Config.string, "TizgBe+sjPzxHZKp7eQwwgd78xjELdu3+NLfNqFZvVNStxezaFr1xkkYHRr2FJDYDsd46xK1UxeC9tlHG5kf9xkUc8n9Ompf5sr1DQaxbmQ=");
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        App42API.initialize(context, apiKey, apiSecret);
        App42CacheManager.setPolicy(App42CacheManager.Policy.NETWORK_FIRST);
        App42CacheManager.setExpiryInMinutes(Config.CACHE_EXPIRE);

        this.userService = App42API.buildUserService();
        this.storageService = App42API.buildStorageService();
        this.uploadService = App42API.buildUploadService();
    }

    public static AsyncApp42ServiceApi instance(Context context) {

        if (mInstance == null) {
            mInstance = new AsyncApp42ServiceApi(context);
        }

        return mInstance;
    }


    /*
    To get User Roles
     */
    public void getRolesByUser(final String userName,
                               final App42CallBack callBack) {
        final Handler callerThreadHandler = new Handler();
        new Thread() {
            @Override
            public void run() {
                try {
                    final App42Response response = userService.getRolesByUser(userName);

                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSuccess(response);
                        }
                    });
                } catch (final App42Exception ex) {
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) {
                                callBack.onException(ex);
                            }
                        }
                    });
                }
            }
        }.start();
    }


    public User createUser(final String name, final String pswd,
                           final String email, final ArrayList<String> roleList, final App42CallBack callBack) {
        final User[] user = {null};

        final Handler callerThreadHandler = new Handler();
        new Thread() {
            @Override
            public void run() {
                try {
                    user[0] = userService.createUser(name, pswd, email, roleList);
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSuccess(user[0]);
                        }
                    });
                } catch (final App42Exception ex) {
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) {
                                callBack.onException(ex);
                            }
                        }
                    });

                }
            }
        }.start();

        return user[0];
    }

    public void createOrUpdateUser(final User profile, final App42CallBack callBack) {

        final Handler callerThreadHandler = new Handler();
        new Thread() {
            @Override
            public void run() {
                try {
                    final Object response = userService.createOrUpdateProfile(profile);
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSuccess(response);
                        }
                    });
                } catch (final App42Exception ex) {
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) {
                                callBack.onException(ex);
                            }
                        }
                    });

                }
            }
        }.start();
    }

    public void changePassword(final String userName, final String oldPassword, final String confirmPassword, final App42CallBack callBack) {

        final Handler callerThreadHandler = new Handler();
        new Thread() {
            @Override
            public void run() {
                try {
                    final Object response = userService.changeUserPassword(userName, oldPassword, confirmPassword);
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSuccess(response);
                        }
                    });
                } catch (final App42Exception ex) {
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) {
                                callBack.onException(ex);
                            }
                        }
                    });

                }
            }
        }.start();
    }


    public void authenticateUser(final String name, final String pswd,
                                 final App42CallBack callBack) {
        final Handler callerThreadHandler = new Handler();
        new Thread() {
            @Override
            public void run() {
                try {
                    final User response = userService.authenticate(name, pswd);
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSuccess(response);
                        }
                    });
                } catch (final App42Exception ex) {
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) {
                                callBack.onException(ex);
                            }
                        }
                    });
                }
            }
        }.start();
    }


    public void getUser(final String name, final App42CallBack callBack) {
        final Handler callerThreadHandler = new Handler();
        new Thread() {
            @Override
            public void run() {
                try {
                    final User response = userService.getUser(name);
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSuccess(response);
                        }
                    });
                } catch (final App42Exception ex) {
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) {
                                callBack.onException(ex);
                            }
                        }
                    });
                }
            }
        }.start();
    }

    public void insertJSONDoc(final String dbName, final String collectionName,
                              final JSONObject json, final App42StorageServiceListener callBack) {
        final Handler callerThreadHandler = new Handler();
        new Thread() {
            @Override
            public void run() {
                try {
                    final Storage response = storageService.insertJSONDocument(dbName, collectionName, json);
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onDocumentInserted(response);
                        }
                    });
                } catch (final App42Exception ex) {
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) {
                                callBack.onInsertionFailed(ex);
                            }
                        }
                    });
                }
            }
        }.start();
    }

    public void findDocByDocId(final String dbName, final String collectionName,
                               final String docId, final App42StorageServiceListener callBack) {
        final Handler callerThreadHandler = new Handler();
        new Thread() {
            @Override
            public void run() {
                try {
                    final Storage response = storageService.findDocumentById(dbName, collectionName, docId);
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onFindDocSuccess(response);
                        }
                    });
                } catch (final App42Exception ex) {
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) {
                                callBack.onFindDocFailed(ex);
                            }
                        }
                    });
                }
            }
        }.start();
    }

    public void findDocByDocIdApp42CallBack(final String dbName, final String collectionName,
                                            final String docId, final App42CallBack app42CallBack) {
        final Handler callerThreadHandler = new Handler();
        new Thread() {
            @Override
            public void run() {
                try {
                    final Storage response = storageService.findDocumentById(dbName, collectionName, docId);
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            app42CallBack.onSuccess(response);
                        }
                    });
                } catch (final App42Exception ex) {
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (app42CallBack != null) {
                                app42CallBack.onException(ex);
                            }
                        }
                    });
                }
            }
        }.start();
    }

    public void findDocumentByKeyValue(final String dbName, final String collectionName,
                                       final String strKey, final String strValue, final App42StorageServiceListener callBack) {
        final Handler callerThreadHandler = new Handler();
        new Thread() {

            @Override
            public void run() {
                try {
                    final Storage response = storageService.findDocumentByKeyValue(dbName, collectionName, strKey, strValue);
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onFindDocSuccess(response);
                        }
                    });
                } catch (final App42Exception ex) {
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) {
                                callBack.onFindDocFailed(ex);
                            }
                        }
                    });
                }
            }
        }.start();
    }

    /*public void updateDocByKeyValue(final String dbName,
                                    final String collectionProvider, final String key, final String value,
                                    final JSONObject newJsonDoc, final App42CallBack callBack) {
        final Handler callerThreadHandler = new Handler();
        new Thread() {
            @Override
            public void run() {
                try {
                    final Storage response = storageService.updateDocumentByKeyValue(dbName, collectionProvider, key, value, newJsonDoc);
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSuccess(response);
                        }
                    });
                } catch (final App42Exception ex) {
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) {
                                callBack.onException(ex);
                            }
                        }
                    });
                }
            }
        }.start();
    }*/

    //addOrUpdateKeys(dbName, collectionProvider, docId, keys,
    public void updateDocPartByKeyValue(final String dbName,
                                        final String collectionName, final String key,
                                        final JSONObject newJsonDoc, final App42CallBack callBack) {
        //final String value,
        final Handler callerThreadHandler = new Handler();
        new Thread() {
            @Override
            public void run() {
                try {
                    final Storage response = storageService.addOrUpdateKeys(dbName, collectionName, key, newJsonDoc);
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSuccess(response);
                        }
                    });
                } catch (final App42Exception ex) {
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) {
                                callBack.onException(ex);
                            }
                        }
                    });
                }
            }
        }.start();
    }

    /*
     * This function Uploads File On App42 Cloud.
	 */
   /* public void uploadImage(final String name,
                            final String filePath, final UploadFileType fileType, final String description, final App42UploadServiceListener callBack) {
        final Handler callerThreadHandler = new Handler();
        new Thread() {
            @Override
            public void run() {
                try {
                    final Upload response = uploadService.uploadFile(name, filePath, UploadFileType.IMAGE, description);
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onUploadImageSuccess(response, name, null);
                        }
                    });
                } catch (final App42Exception ex) {
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) {
                                callBack.onUploadImageFailed(ex);
                            }
                        }
                    });
                }
            }
        }.start();
    }*/

    public void uploadFile(final String name,
                           final String filePath, final UploadFileType fileType, final String description, final App42CallBack callBack) {
        final Handler callerThreadHandler = new Handler();
        new Thread() {
            @Override
            public void run() {
                try {
                    final Upload response = uploadService.uploadFile(name, filePath, fileType, description);
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSuccess(response);
                        }
                    });
                } catch (final App42Exception ex) {
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) {
                                callBack.onException(ex);
                            }
                        }
                    });
                }
            }
        }.start();
    }

   /* public void getImage(final String fileName, final App42UploadServiceListener callBack) {
        final Handler callerThreadHandler = new Handler();
        new Thread() {
            @Override
            public void run() {
                try {
                    final Upload response = uploadService.getFileByName(fileName);
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onGetImageSuccess(response);
                        }
                    });
                } catch (final App42Exception ex) {
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) {
                                callBack.onGetImageFailed(ex);
                            }
                        }
                    });
                }
            }
        }.start();
    }*/

    public void getFile(final String fileName, final App42UploadServiceListener callBack) {
        final Handler callerThreadHandler = new Handler();
        new Thread() {
            @Override
            public void run() {
                try {
                    final Upload response = uploadService.getFileByName(fileName);
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSuccess(response);
                        }
                    });
                } catch (final App42Exception ex) {
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) {
                                callBack.onException(ex);
                            }
                        }
                    });
                }
            }
        }.start();
    }

    /*
     * This function Uploads File On App42 Cloud.
	 */
    public void getImageByUser(final String fileName, final String userName, final App42CallBack app42CallBack) {
        final Handler callerThreadHandler = new Handler();
        new Thread() {
            @Override
            public void run() {
                try {
                    final Upload response = uploadService.getFileByUser(fileName, userName);
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            app42CallBack.onSuccess(response);
                        }
                    });
                } catch (final App42Exception ex) {
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (app42CallBack != null) {
                                app42CallBack.onException(ex);
                            }
                        }
                    });
                }
            }
        }.start();
    }

    /*
     * This function get Files by user On App42 Cloud.
	 */
    public void getAllFilesByUser(final String userName, final App42CallBack callBack) {
        final Handler callerThreadHandler = new Handler();
        new Thread() {
            @Override
            public void run() {
                try {
                    final Upload response = uploadService.getAllFilesByUser(userName);
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSuccess(response);
                        }
                    });
                } catch (final App42Exception ex) {
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) {
                                callBack.onException(ex);
                            }
                        }
                    });
                }
            }
        }.start();
    }

    /*
	 * This function Uploads File On App42 Cloud.
	 */
    public void uploadImageForUser(final String name, final String userName,
                                   final String filePath, final UploadFileType fileType, final String description, final App42CallBack callBack) {
        final Handler callerThreadHandler = new Handler();
        new Thread() {
            @Override
            public void run() {
                try {
                    final Upload response = uploadService.uploadFileForUser(name, userName, filePath, fileType, description);
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSuccess(response); //, name, userName
                        }
                    });
                } catch (final App42Exception ex) {
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) {
                                callBack.onException(ex);
                            }
                        }
                    });
                }
            }
        }.start();
    }

    /*
     * This function Uploads File On App42 Cloud.
     */
    public void removeImageByUser(final String fileName, final String userName,
                                  final App42CallBack callBack) {
        final Handler callerThreadHandler = new Handler();
        new Thread() {
            @Override
            public void run() {
                try {
                    final App42Response response = uploadService.removeFileByUser(fileName, userName);

                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSuccess(response);
                        }
                    });
                } catch (final App42Exception ex) {
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) {
                                callBack.onException(ex);
                            }
                        }
                    });
                }
            }
        }.start();
    }

    public void getImageCount(final String userName,
                              final App42CallBack callBack) {
        final Handler callerThreadHandler = new Handler();
        new Thread() {
            @Override
            public void run() {
                try {
                    final App42Response response = uploadService.getAllFilesCountByUser(userName);

                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSuccess(response);
                        }
                    });
                } catch (final App42Exception ex) {
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) {
                                callBack.onException(ex);
                            }
                        }
                    });
                }
            }
        }.start();
    }

   /* public interface App42UserServiceListener {
        void onUserCreated(User response);

        void onCreationFailed(App42Exception exception);

        void onGetUserSuccess(User response);

        void onGetUserFailed(App42Exception exception);

        void onUserAuthenticated(User response);

        void onAuthenticationFailed(App42Exception exception);

    }*/


    public interface App42StorageServiceListener {

        void onDocumentInserted(Storage response);

        void onUpdateDocSuccess(Storage response);

        void onFindDocSuccess(Storage response);

        void onInsertionFailed(App42Exception ex);

        void onFindDocFailed(App42Exception ex);

        void onUpdateDocFailed(App42Exception ex);
    }


    public interface App42UploadServiceListener {
        void onUploadImageSuccess(Upload response, String fileName, String userName);

        void onUploadImageFailed(App42Exception ex);

        void onGetImageSuccess(Upload response);

        void onGetImageFailed(App42Exception ex);

        void onSuccess(Upload response);

        void onException(App42Exception ex);

        void onSuccess(Object o);

        void onException(Exception e);
    }
}