# Project 4 - *Parstagram*

**Parstagram** is a photo sharing app using Parse as its backend.

Time spent: **16~20** hours spent in total

## User Stories

The following **required** functionality is completed:

- [x] User sees app icon in home screen.
- [x] User can sign up to create a new account using Parse authentication
- [x] User can log in and log out of his or her account
- [x] The current signed in user is persisted across app restarts
- [x] User can take a photo, add a caption, and post it to "Instagram"
- [x] User can view the last 20 posts submitted to "Instagram"
- [x] User can pull to refresh the last 20 posts submitted to "Instagram"
- [x] User can tap a post to view post details, including timestamp and caption.

The following **stretch** features are implemented:

- [approx] Style the login page to look like the real Instagram login page.
- [approx] Style the feed to look like the real Instagram feed.
- [x] User should switch between different tabs - viewing all posts (feed view), capture (camera and photo gallery view) and profile tabs (posts made) using fragments and a Bottom Navigation View.
- [x] User can load more posts once he or she reaches the bottom of the feed using endless scrolling.
- [x] Show the username and creation time for each post
- [ ] After the user submits a new post, show an indeterminate progress bar while the post is being uploaded to Parse
- User Profiles:
  - [x] Allow the logged in user to add a profile photo
  - [x] Display the profile photo with each post
  - [x] Tapping on a post's username or profile photo goes to that user's profile page
  - [x] User Profile shows posts in a grid view
- [ ] User can comment on a post and see all comments for each post in the post details screen.
- [x] User can like a post and see number of likes for each post in the post details screen.

The following **additional** features are implemented:

- [x] Implemented gallery selector

Please list two areas of the assignment you'd like to **discuss further with your peers** during the next class (examples include better ways to implement something, how to extend your app in certain ways, etc):

1. Implementing a grid view similar to instagram -> includes just photos 
2. Implementing the follow feature

## Video Walkthrough

Here's a walkthrough of implemented user stories

<img src='demo11-min.gif' title='Video Walkthrough1' width='' alt='Video Walkthrough' />
<img src='demo22-min.gif' title='Video Walkthrough2' width='' alt='Video Walkthrough' />
<img src='scrolling.gif' title='Video Walkthrough3' width='' alt='Video Walkthrough' />
<img src='refresh.gif' title='Video Walkthrough4' width='' alt='Video Walkthrough' />
<img src='user_persistence.gif' title='Video Walkthrough5' width='' alt='Video Walkthrough' />


GIF created with [Kap](https://getkap.co).

## Credits

List an 3rd party libraries, icons, graphics, or other assets you used in your app.

- [Android Async Http Client](http://loopj.com/android-async-http/) - networking library


## Notes

- I implemented liking both in details and timeline.
- I have a linear profile if accessed through bottom navigation and grid-view profile if accessed through username/profile picture.

## License

    Copyright [2020] [Zaynab Ghazi]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
