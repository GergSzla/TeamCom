package ie.wit.teamcom.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.firebase.database.ValueEventListener
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Channel
import kotlinx.android.synthetic.main.fragment_settings.view.*
import org.jetbrains.anko.AnkoLogger

class SettingsFragment : Fragment(), AnkoLogger {

    lateinit var app: MainApp
    lateinit var eventListener : ValueEventListener
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
            navigateTo(RoleListFragment.newInstance(currentChannel!!))
        }

        root.txtSettingsMembers.setOnClickListener {

        }

        root.txtSettingsInvites.setOnClickListener {
            navigateTo(InvitesListFragment.newInstance(currentChannel!!))
        }

        return root
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