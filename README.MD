# TeamCom
![Logo.png](https://res.cloudinary.com/dkdptqakb/image/upload/v1617276608/Fourth%20Year%20Project/logo_gif.gif)

## Description:
What is TeamCom?

TeamCom is a multi-purpose app designed for companies and colleges, focusing primarily on improving collaboration, communication and mental well-being in professional environments by providing task and team management tools. 
With the rise of remote working, there is a growing need for applications like TeamCom. TeamCom is a channel-based application similar to Slack or Discord and can be set up by anyone.
Channel users are able to create projects and tasks for these projects, personal and public-shared reminders and public channel events, schedule online or in-person meeting and join meetings. TeamCom also offers conversation functionalities with other channel members. Private and group conversations can be easily created between channel members. 

TeamCom offers personal assistance and advice to all members in all channels and actively looks out for users’ mental well-being by conducting frequent analyses on meetings, reminders and tasks assigned to users to help with work-life balance. 
 
## Walkthrough:
### Login & Registration
TeamCom offers two forms of login to its users. Users can login using their Google accounts with ease [Recommended] or by creating a new account in Firebase by pressing the *Create an Account* button and filling out the registration form. 
Once the user has successfully logged in, they can edit their profile or allow the app to automatically login by pressing the *Cog* icon on their profile cards as shown in the next section.

![](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1617294929/Fourth%20Year%20Project/item_login.jpg)

### Channels
Once the user has logged in successfully they will be presented with a list of channels that they are part of along with their *TeamCom Profile Card*. Users can edit their profile or allow the app to automatically login by pressing the *Cog* icon on their profile cards.
##### Join / Create
 **To Create a Channel**, all the user has to do is press the floating button with the *Plus* icon and choose *Create a Channel*. Users will be required to give it a name and short description, but images can be kept as default.
 **To Join a Channel**, an invitation code from the channel in question is required. To receieve this code, the user must ask someone in the channel to create an invite and send it to them. ***(more on this in the Invites section)***.
 
![](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1617294897/Fourth%20Year%20Project/item_channels.jpg) ![](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1617294894/Fourth%20Year%20Project/item_channel_create.jpg) ![](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1617294899/Fourth%20Year%20Project/item_channel_join.jpg) 


### News Feed
Once a user has joined or created a channel successfully, they will then be able send public posts on the news feed in the app. These posts can be seen, liked and commented on by anyone with access to the channel as seen in the screenshots bellow where two seperate accounts were used.

![](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1617294886/Fourth%20Year%20Project/item_post.jpg) ![](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1617294901/Fourth%20Year%20Project/item_comment.jpg)

### Conversations
The app includes the ability to create private and group conversations within the channels. This can simply done by opening the side menu in the app, tapping *conversations* -which will present the user with a list of conversations-, then by pressing the floating *plus* icon ***(Shown in first gif below)***. 

![](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1617294923/Fourth%20Year%20Project/item_conversation_create_gif.gif) ![](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1617294919/Fourth%20Year%20Project/item_conversation_reply_gif.gif)

### Meetings
The app allows its channel users to schedule online or in-person meetings. This can be done by tapping *meetings* in the side menu.
Online meetings that have *ZOOM* as their selected platform can be joined through the app via the *ZOOM SDK*. Other online meetings will be redirected to the appropriate website.

![](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1617294927/Fourth%20Year%20Project/item_meeting_create_gif.gif) ![](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1617294891/Fourth%20Year%20Project/item_view_meeting.jpg) 

### Projects 
Channel Projects are projects that will have a series of tasks within them. These projects can be created and accessed by opening the side menu and pressing *projects*, which will open the projects list . . . 
##### Task Manager
. . . tapping into these projects will display the tasks within in it that are required to complete the project. Tasks can be created by anyone with the appropriate permissions ***(more in the Settings/Roles section)***

![](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1617294924/Fourth%20Year%20Project/item_task_gif.gif) ![](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1617294901/Fourth%20Year%20Project/item_tasks_change_stage.gif) 

### Calendar
The calendar allows users to create public events in the channel which can be seen by everyone. 

![](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1617294930/Fourth%20Year%20Project/item_calendar_create.gif) ![](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1617294928/Fourth%20Year%20Project/item_calendar_view.gif) 

### Reminders


![](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1617294886/Fourth%20Year%20Project/item_reminders.jpg) ![](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1617294889/Fourth%20Year%20Project/item_assistant_notification.jpg)

### Settings 
The settings menu can be access by opening the side menu and tapping *settings*. Here users can access *Roles, Members, Invites, change Channel Details and make changes to personal Survey Settings*

![](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1617294891/Fourth%20Year%20Project/item_settings.jpg)
##### Roles
By tapping into Roles, the user will be shown a list of the current available Roles. Each of these roles contains a list of permissions that decide what the user with the role is able to do in the channel.

![](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1617294886/Fourth%20Year%20Project/item_roles_2.jpg) ![](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1617294930/Fourth%20Year%20Project/item_create_roles.jpg) ![](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1617294885/Fourth%20Year%20Project/item_roles.jpg)

##### Invites
Channel invites can be viewed in the *Settings/Invites* section within the app. This page will allow users to view existing invitations or create new ones. Creating new invites will require the creator to insert an expiration date and a use limit which determines if the invite is still valid. 
These invitation codes can then be copied by long-pressing the invitation card. 

![](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1617294923/Fourth%20Year%20Project/item_invite_copied.jpg)


##### Survey Settings
Users can opt into a survey via *Settings>Survey* which will ask the user a series of questions about how they are feeling to assess their mental well-being. The results of this recurring survey (frequency can be defined by user here), allows the Personal Assistant ***(see Personal Assistant section)*** to come up with better suggestions for the users.

![](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1617301212/Fourth%20Year%20Project/item_survey.jpg)


### Logs
![](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1617294887/Fourth%20Year%20Project/item_logs.jpg)


### Personal Assistant
To balance work-life pressures, a personal assistant option is available to analyse the number of tasks a user has due and make suggestions to the task assigner to perhaps reschedule to a different date to relieve the assignee from pressure. In case of meetings, the assistant will ask the user if they wish to reschedule.


![](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1617294889/Fourth%20Year%20Project/item_assistant_notification.jpg) ![](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1617301475/Fourth%20Year%20Project/item_assistant_notification_2.jpg)


### Notifications
![](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1617294886/Fourth%20Year%20Project/item_notifications.jpg)
