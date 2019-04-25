package com.makerloom.golearn.utils

import android.text.TextUtils
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.makerloom.golearn.models.UniversityInfo
import com.makerloom.golearn.models.UserInfo
import io.paperdb.Paper

class UserUtils {
    companion object {
        val TAG = UserUtils::class.java.simpleName

        fun submitUserInfo (user: FirebaseUser, fName: String, lName: String, submitUserInfoCallback: UserInfoCallback) {
            if (TextUtils.isEmpty(fName) || TextUtils.isEmpty(lName)) {
                return submitUserInfoCallback
                        .onFailure(Exception("The User's First Name or Last Name is Empty"))
            }

            val db = FirebaseFirestore.getInstance()
            Commons.enableFirestoreOffline(db)

            val ref = db.collection(Commons.USER_INFO_COLLECTION_NAME)
                    .document(user.uid)

            val userInfo = UserInfo(fName, lName)
            ref.set(userInfo)
                    .addOnFailureListener {
                        submitUserInfoCallback.onFailure(it)
                    }
                    .addOnSuccessListener {
                        cacheUserInfo(userInfo)
                        submitUserInfoCallback.onSuccess(userInfo)
                    }
        }

        fun submitUniversityInfo (user: FirebaseUser, uni: String, dept: String, level: String, submitUniversityInfoCallback: UniversityInfoCallback) {
            val u = TextUtils.isEmpty(uni)
            val d = TextUtils.isEmpty(dept)
            val l = TextUtils.isEmpty(level)

            if (u || d || l) {
                return submitUniversityInfoCallback
                        .onFailure(Exception("The University's Name, Department or Level is Empty"))
            }

            val db = FirebaseFirestore.getInstance()
            Commons.enableFirestoreOffline(db)

            val ref = db.collection(Commons.UNIVERSITY_INFO_COLLECTION_NAME)
                    .document(user.uid)

            val uniInfo = UniversityInfo(uni, dept, level)
            ref.set(uniInfo)
                    .addOnFailureListener {
                        submitUniversityInfoCallback.onFailure(it)
                    }
                    .addOnSuccessListener {
                        cacheUniversityInfo(uniInfo)
                        submitUniversityInfoCallback.onSuccess(uniInfo)
                    }
        }

        fun getUserInfo (user: FirebaseUser, getUserInfoCallback: UserInfoCallback) {
            val db = FirebaseFirestore.getInstance()
            Commons.enableFirestoreOffline(db)

            val ref = db.collection(Commons.USER_INFO_COLLECTION_NAME)
                    .document(user.uid)

            ref.get()
                    .addOnFailureListener {
                        getUserInfoCallback.onFailure(it)
                    }
                    .addOnSuccessListener {
                        if (it != null && it.exists()) {
                            val userInfo = it.toObject(UserInfo::class.java)
                            if (null == userInfo) {
                                // getUserInfoCallback.onFailure(Exception("The UserPINInfo Object is null"))
                                Log.d(TAG, "The UserInfo Object is null")
                                getUserInfoCallback.onSuccess(null)
                                return@addOnSuccessListener
                            }

                            cacheUserInfo(userInfo)
                            getUserInfoCallback.onSuccess(userInfo)
                        }
                        else {
                            // Return null if document doesn't exist
                            getUserInfoCallback.onSuccess(null)
                        }
                    }
        }

        fun getUniversityInfo (user: FirebaseUser, getUniversityInfoCallback: UniversityInfoCallback) {
            val db = FirebaseFirestore.getInstance()
            Commons.enableFirestoreOffline(db)

            val ref = db.collection(Commons.UNIVERSITY_INFO_COLLECTION_NAME)
                    .document(user.uid)

            ref.get()
                    .addOnFailureListener {
                        getUniversityInfoCallback.onFailure(it)
                    }
                    .addOnSuccessListener {
                        if (it != null && it.exists()) {
                            val uniInfo = it.toObject(UniversityInfo::class.java)
                            if (null == uniInfo) {
                                // getUniversityInfoCallback.onFailure(Exception("The UniversityInfo Object is null"))
                                Log.d(TAG, "The UniversityInfo Object is null")
                                getUniversityInfoCallback.onSuccess(null)
                                return@addOnSuccessListener
                            }

                            cacheUniversityInfo(uniInfo)
                            getUniversityInfoCallback.onSuccess(uniInfo)
                        }
                        else {
                            // Return null if document doesn't exist
                            getUniversityInfoCallback.onSuccess(null)
                        }
                    }
        }

        fun cacheUserInfo (userInfo: UserInfo) {
            Paper.book().write(Commons.USER_INFO_COLLECTION_NAME, userInfo)
        }

        fun getCachedUserInfo (): UserInfo? {
            val userInfo: UserInfo? = Paper.book().read(Commons.USER_INFO_COLLECTION_NAME)
            return userInfo
        }

        fun cacheUniversityInfo (universityInfo: UniversityInfo) {
            Paper.book().write(Commons.UNIVERSITY_INFO_COLLECTION_NAME, universityInfo)
        }

        fun getCachedUniversityInfo (): UniversityInfo? {
            val universityInfo: UniversityInfo? = Paper.book()
                    .read(Commons.UNIVERSITY_INFO_COLLECTION_NAME)
            return universityInfo
        }
    }

    interface UserInfoCallback {
        fun onSuccess (userInfo: UserInfo?)

        fun onFailure (ex: Exception)
    }

    interface UniversityInfoCallback {
        fun onSuccess (universityInfo: UniversityInfo?)

        fun onFailure (ex: Exception)
    }
}