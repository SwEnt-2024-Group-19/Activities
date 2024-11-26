# Milestone M2: Team Feedback

This milestone M2 provides an opportunity to give you, as a team, formal feedback on how you are performing in the project. By now, you should be building upon the foundations set in M1, achieving greater autonomy and collaboration within the team. This is meant to complement the informal, ungraded feedback from your coaches given during the weekly meetings or asynchronously on Discord, email, etc.

The feedback focuses on two major themes:
First, whether you have adopted good software engineering practices and are making progress toward delivering value to your users.
Is your design and implementation of high quality, easy to maintain, and well tested?
Second, we look at how well you are functioning as a team, how you organize yourselves, and how well you have refined your collaborative development.
An important component is also how much you have progressed, as a team, since the previous milestone.
You can find the evaluation criteria in the [M2 Deliverables](https://github.com/swent-epfl/public/blob/main/project/M2.md) document.
As mentioned in the past, the standards for M2 are elevated relative to M1, and this progression will continue into M3.

We looked at several aspects, grouped as follows:

 - Design
   - [Features](#design-features)
   - [Design Documentation](#design-documentation)
 - [Implementation and Delivery](#implementation-and-delivery)
 - Scrum
   - [Backlogs Maintenance](#scrum-backlogs-maintenance)
   - [Documentation and Ceremonies](#scrum-documentation-and-ceremonies)
   - [Continuous Delivery of Value](#scrum-continuous-delivery-of-value)

## Design: Features

We interacted with your app from a user perspective, assessing each implemented feature and flagging any issues encountered. Our evaluation focused mainly on essential features implemented during Sprints 3, 4, and 5; any additional features planned for future Sprints were not considered in this assessment unless they induced buggy behavior in the current APK.
We examined the completeness of each feature in the current version of the app, and how well it aligns with user needs and the overall project goals.


**Features**

Your app starts to shape up really well and you use differents sensors to give a more customized and enjoyable experience to the user, congrats ! 
The features you implemented are relevant for your project, but some parts could be a bit more polished / finished (Markers on the map could be clickable, etc..). 
In general the features are good but you could start to make it a bit more clean (UI/UX) so that the work that you have put in your project shines more to the end user. 


For this part, you received 6.3 points out of a maximum of 8.0.

## Design: Documentation

We reviewed your Figma (including wireframes and mockups) and the evolution of your overall design architecture in the three Sprints.
We assessed how you leveraged Figma to reason about the UX, ensure a good UX, and facilitate fast UI development.
We evaluated whether your Figma and architecture diagram accurately reflect the current implementation of the app and how well they align with the app's functionality and structure.


**Figma**:
The figma seems to be a bit incoherent in some places. For example, the navbar doesn't contain the same elements on every pages and the mockup doesn't contain the same pages as the wireframe. The wireframe seems to be up to date, but please do the same next time for the mockup and try to link the page dynamically (shouldn't take too long to do).

**Architecture diagram**:
The architecture still contains the main parts of your app, but you could try to make it more precise by specifying the different components more clearly (Firestore, Firestore storage, Nominatim, ...)


For this part, you received 3.3 points out of a maximum of 6.0.

## Implementation and Delivery

We evaluated several aspects of your app's implementation, including code quality, testing, CI practices, and the functionality and quality of the APK.
We assessed whether your code is well modularized, readable, and maintainable.
We looked at the efficiency and effectiveness of your unit and end-to-end tests, and at the line coverage they achieve.


**Code quality**:
You made some improvements compared to the first milestone, but there are still some things that could be improved:
- Don't hesitate to write a bit more documentation in your code 
-  You considerably reduced the amount of commented code in your main, but there are still some small occurences, which could be avoided
- You made some efforts modularizing your code, but it can be even more pronounced. For example, you have a folder in the UI directory called "components" and you currently only use it for the textfields, but this concept can be applied to the whole UI of your project so that you can reuse components without rewriting them each time. This is also the case for some functions/composable, you can divide them into smaller functions to make it easier to read.

**Testing and CI:**
- Your CI is properly configured to run the tests and fails if needed.
- The line coverage of your repo found on sonar is above 80%, good job !
- You have two end-to-end test that starts from the entry point of your application, but they are mostly checking that objects are correctly displayed without interacting too much with them (creating activities, etc...). Otherwise congratulations on using DI, it took you a lot of time to set it up and we hope you find it useful !

**Testing your APK**:
*Those are mainly advices, your app has good functionalities and we did not encounter a lot of bugs while using it.*

- You could make a separate CI workflow so that you can chose when the APK is built. This could help you save some time of execution for your CI.
- Work a bit on the UI/UX and add maybe an onboarding that explains to the user how to use the app -> what are the different categories, etc... Also in the forms, it might be more convenient for the user to use inputs such as TimePickers for the dates, times and delays. 
- There is a google map feature that could be better for showing the user's position than a blue marker (it is relatively easy to activate once you have asked for the position permission)
- Use a similar layout for the liked and profile pages when you are not logged in -> UI coherence of the app
- I could not create an account using the email password option -> toast with error appeared, but worked well with google sign-in
- Permissions for camera are not well managed, if I decline and try to take a picture, there is an error. You could also prompt for permission when clicking on the button instead of at the launch of the app.
- I quit the profile creation and came back to the app and this special case was well handled, congratulations !
- Predefined categories to choose from on the account creation page would be better for your app (filters, ...)
- You handled the case properly :  i cannot enroll twice in an activity, but could be nice to disable the button or turn it into a functionality to quit the activity instead of an error toast.
- Link the marker on the map so that when I click on one, i get redirected to the activitie's page.


Congratulations, the APK was way more usable than in M1 !


For this part, you received 12.6 points out of a maximum of 16.0.

## Scrum: Backlogs Maintenance

We looked at whether your Scrum board is up-to-date and well organized.
We evaluated your capability to organize Sprint 6 and whether you provided a clear overview of this planning on the Scrum board.
We assessed the quality of your user stories and epics: are they clearly defined, are they aligned with a user-centric view of the app, and do they suitably guide you in delivering the highest value possible.


**Sprint backlog**

If we look at your sprint backlog for sprint 6, the hours you wrote for the tasks are not sufficient for a full sprint of work. Only 1 tasks was assigned per student and since a task is supposed to take around 4 hours, there should be more of them. Also, please make sure to clean the column at the end of each sprint and remove tasks that are marked as finished. (issue #136 should not have been there)

**Product backlog**

The product backlog is still relevant and contains some good user stories, but we also found a finish task in it, which should have never been there in the first place. Some of the title also doesn't follow the format of the rest of your user stories : "implement a search bar in the overview (the activities that match the most are at the top)"



For this part, you received 2.2 points out of a maximum of 4.0.

## Scrum: Documentation and Ceremonies

We assessed how you used the Scrum process to organize yourselves efficiently.
We looked at how well you documented your team Retrospective and Stand-Up during each Sprint.
We also evaluated your autonomy in using Scrum.


**Scrum documents**
Your scrum documents are generally well completed with some detailed explanation on what you are going through. The stand-up document of sprint 3 has some missing values, please write it down if someone wasn't able to join and the "how are you going to resolve your blockers" is not really completed (almost same values every weeks)

**Meetings with the coaches**
Meetings are going better and better each time and are in general well-structured ! Thank you for this and let's continue this way

**Autonomy during meetings**
Your team showed a great autonomy during our friday's sessions, congratulations ! The scrum master have led the meetings in a fluent and efficient way !


For this part, you received 3.5 points out of a maximum of 4.0.

## Scrum: Continuous Delivery of Value

We evaluated the Increment you delivered at the end of each Sprint, assessing your team’s ability to continuously add value to the app.
This included an assessment of whether the way you organized the Sprints was conducive to an optimal balance between effort invested and delivery of value.


Your team is generally consistent in the produced work, even during difficult weeks with midterm, good job !


For this part, you received 1.8 points out of a maximum of 2.0.

## Summary

Based on the above points, your intermediate grade for this milestone M2 is 4.71. If you are interested in how this fits into the bigger grading scheme, please see the [project README](https://github.com/swent-epfl/public/blob/main/project/README.md) and the [course README](https://github.com/swent-epfl/public/blob/main/README.md).

Your coaches will be happy to discuss the above feedback in more detail.

Good luck for the next Sprints!
