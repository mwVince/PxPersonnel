# PxPersonnel
Shifts-scheduling system with human resources functionalities in Java

## Story
Things started when I worked at a warehouse while waiting for my military process, to be processed. At the time, we need to schedule out break every month with certain rules. Mainly, no more than 2 forklift drivers, and 4 regular workers can take break at the same day. Sunday we don't work, and you choose on day from Monday to Saturday.

Process is like this: eveyone line-up at the manager's desk, and declare what days you intend to take break for the next month. The manager will mark your break on a printed table for everyone. Then the manager figure out which days break the rule, and decide who need to give up the break on that day, and re-choose thr break. After all, the manger will publish the final break schedule for next month.

You might think, this is dumb; at least I do. But this is actully happening in the company I worked at, and many other companies in manufacturing/service industry. So I write this project to automate and speed up this shift scheduling process, and feel free to take it if your business is in the similar situation, and modify it for your need (maybe there are 20 poeple allowed to take break for your businee).

## How to Use
### Initial Process
Run `DBRenewer.java` to initiate necessary databaase objects
Run `EmployeeSampler.java` if you want to see how this work (take a look at this file can grant you some idea of the employee database)

Notice `admin ID / admin password` are both default to `999999`. It can be change in `Parameter.java` manually before configuration.

### Standalone
To run PxPersonnel in standalone means every changes is done locally in one enviroment. That is, employee / manager use the same computer to make operation one by one. It's not the most efficient way to do thing, but if your company deosn't want to set up public server for this usage, or doesn't want to share the local network with employee, the standalone can be used to simply speed up the paper-based process.

Run `PxPersonnelStandalone.java` will start the standalone app. Following the prompts to log in and do everything you want to do about it. The system will decide the login role accroding to login information, and provide different funcationailities accordingly.

### Client/Server
To run PxPersonnel in client/server mode allows users to use Pxpersonnel simmultaneously with differen machines. By doing so, employees can declare their breaks at anytime. Company do need to set up the server on either public network or local network.

On the machine that serves as Pxpersonnel server, run `PxPersonnelServer.java` to start the server process. The default `port` is set to `7733`, please adjust as you wish. Note the `ip` address from the server, either in public or local setting. If decide to set up for local use, then client can only connect through local network.

On the users' machine, adjust `PxPersonnelClient.java` for the correct `ip` and `port` of the server. Following the prompts to log in and do everything you want to do about it. The system will decide the login role accroding to login information, and provide different funcationailities accordingly.

## Example Workflow
1. Manager needs to add a month to the calender, and `current month` will be set to input month.
2. Manager needs to allow choose break, meaning enter a `choose break period`.
3. Once choose break is allowed, employee can log in and declare their break for `current month`
4. Once all employee have chosen break, manager can run `break lottery` which automatically select unlucky employee who's break need to be re-assigned. And Pxpersonnel will re-assign breaks for them.
5. Manager can export the breaks schdule / shift schedule in csv fromat.

## Other Operation
Some exmplar operation includes:
- Employees can see their break for `current month`
- Managers and move breaks of employee around.
- Managers and add shift / break according to employee's attandence
- Managers can add / edit / view Employee information

## Important Terminology
- `currentMonth`: current month in Pxpersonnel is not the regular month expression. Although data in `Calender` is saved as regular month, like April, when used `currentMonth` 202104, it means from first Monday of April 2021, to last Saturday of week of April 30, 2021.\
It's because, our scheduling is closer to a weekly based system, other than monthly based syetem. Therefore, it is needed to adjust the range of a 'month' to fit the situation. In the above example, `currentMonth` 202104 is 04/05/2021 - 05/01/2021.
