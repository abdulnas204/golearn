package com.makerloom.golearn.screens.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.makerloom.golearn.R
import com.makerloom.golearn.screens.AddUniversityInfoActivity
import com.makerloom.golearn.screens.AddUserInfoActivity
import com.makerloom.golearn.screens.HomeActivity
import com.makerloom.golearn.screens.WelcomeActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SettingsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }

    override fun onResume() {
        super.onResume()
        (this.activity as HomeActivity).setTitle(getString(R.string.title_settings))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editUniBtn = view.findViewById<View>(R.id.edit_uni_info_btn)
        editUniBtn.setOnClickListener {
            startActivity(Intent(this@SettingsFragment.activity, AddUniversityInfoActivity::class.java))
        }

        val editUserBtn = view.findViewById<View>(R.id.edit_user_info_btn)
        editUserBtn.setOnClickListener {
            startActivity(Intent(this@SettingsFragment.activity, AddUserInfoActivity::class.java))
        }

        val signOutBtn = view.findViewById<View>(R.id.sign_out_btn)
        signOutBtn.setOnClickListener {
            AlertDialog.Builder(this@SettingsFragment.activity)
                    .setTitle("Sure?")
                    .setMessage("Are you sure you want to sign out?")
                    .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                        try {
                            FirebaseAuth.getInstance().signOut()
                        }
                        catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                        finally {
                            startActivity(Intent(this@SettingsFragment.activity, WelcomeActivity::class.java))
                        }
                    })
                    .setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                    })
                    .create()
                    .show()
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
                SettingsFragment().apply {
                    arguments = Bundle().apply {
//                        putString(ARG_PARAM1, param1)
//                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
