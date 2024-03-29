package ie.wit.teamcom.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Channel
import kotlinx.android.synthetic.main.fragment_settings.view.*
import org.jetbrains.anko.AnkoLogger

class SettingsFragment : Fragment(), AnkoLogger {

    lateinit var app: MainApp
    lateinit var root: View
    var currentChannel = Channel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

        arguments?.let {
            currentChannel = it.getParcelable("channel_key")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_settings, container, false)
        activity?.title = getString(R.string.title_settings)

        root.txtChannelNameSettings.text = currentChannel.channelName

        root.txtSettingsRoles.setOnClickListener {
            navigateTo(RoleListFragment.newInstance(currentChannel))
        }

        root.txtSettingsMembers.setOnClickListener {
            navigateTo(MembersFragment.newInstance(currentChannel))
        }

        root.txtSettingsInvites.setOnClickListener {
            navigateTo(InvitesListFragment.newInstance(currentChannel))
        }

        root.txtSettingsChannelDetails.setOnClickListener {
            if (app.currentActiveMember.role.perm_admin || app.currentActiveMember.role.perm_edit_channel){
                navigateTo(EditChannelDetails.newInstance(currentChannel))
            } else {
                Toast.makeText(
                    context, "You do not have the permissions to edit this channel!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        root.txtSettingsQuestionnaireRem.setOnClickListener {
            navigateTo(EditSurveySettings.newInstance(currentChannel))
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        app.activityResumed(currentChannel, app.currentActiveMember)
    }

    override fun onPause() {
        super.onPause()
        app.activityPaused(currentChannel, app.currentActiveMember)
    }

    companion object {
        @JvmStatic
        fun newInstance(channel: Channel) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("channel_key", channel)
                }
            }
    }

    private fun navigateTo(fragment: Fragment) {
        val fragmentManager: FragmentManager = activity?.supportFragmentManager!!
        fragmentManager.beginTransaction()
            .replace(R.id.homeFrame, fragment)
            .addToBackStack(null)
            .commit()
    }
}