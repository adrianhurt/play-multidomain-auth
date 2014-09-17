## Multidomain Auth [Play 2.3 - Scala]

This is a second part of [Multidomain Seed](https://github.com/adrianhurt/play-multidomain-seed) project. Please follow the steps described there to run both services (the public web page and the administration web page).

This project tries to be an example of how to implement an Authentication and Authorization layer using the [Silhouette authentication library](http://silhouette.mohiva.com).

The public web page (`www.myweb.com`) implements the typical authentication functionality. You can:

* Sign up (with email confirmation)
* Sign in
* Sign out
* Reset password (via email)
* Control of public and private areas

The administration web page (`admin.myweb.com`) also implements an authorization functionality based on roles . You can:

* Sign in
* Sign out
* Restrict areas to those managers whose roles match with the specified ones (with logic `OR` or `AND`)

### First of all: configure the Mail Plugin

I've used the [Mailer plugin](https://github.com/typesafehub/play-plugins/tree/master/mailer) to send an email to the user for resetting passwords and email confirmation. So you need to configure its parameters in files:

* `conf/shared.def.conf`
* `modules/web/conf/shared.def.conf` (remember it is a copy of `conf/shared.def.conf`)
* `modules/admin/conf/shared.def.conf` (remember it is a copy of `conf/shared.def.conf`)

For example, for a gmail email address:

    mail.from="your@gmail.com"
    smtp {
      host=smtp.gmail.com
      port=587
      user="your@gmail.com"
      password=yourpassword
      ssl=true
    }

### Silhouette

All the authentication and authorization functionalities are implemented using the [Silhouette authentication library](http://silhouette.mohiva.com). Please check the [documentation](http://docs.silhouette.mohiva.com/en/latest/) first.

The main ideas you have to know to understand the code are:

* I haven't used Dependency Injection. I've used a not strict implementation of the [Thin Cake Pattern](http://www.warski.org/blog/2014/02/using-scala-traits-as-modules-or-the-thin-cake-pattern/). I've done this in order to simplify the number of files and traits required.
* I have used some implicit functions to use `LoginInfo` and `PasswordInfo` objects as simple `Strings` and vice versa. It makes clearer the code, but you have to remember that. You can check them at `modules/common/app/utils/silhouette/Implicits.scala`.
* Each subproject has its own Silhouette implementation but they shared some code from the common subproject.
* The corresponding `Auth` controller contains every action related with authentication or authorization.

Let's see some interesting files:

* `modules/common/app/models/User.scala`:  the user class (with its login info: email and encrypted password). All the users are stored dynamically in a HashMap.
* `modules/common/app/models/Manager.scala`:  the manager class (with its login info: email and encrypted password). All the managers are stored dynamically in a HashMap.

* `modules/web/app/models/TokenUser.scala`:  implements a Token for the web page in case to reset a password or confirm a user's email during a sign up. All the tokens are stored dynamically in a HashMap.
* `modules/admin/app/models/TokenManager.scala`:  implements a Token for the admin page in case to reset a password. All the tokens are stored dynamically in a HashMap.

* `modules/common/app/utils/silhouette/SilhouetteController.scala`:  declares the general Controller that gives all the required functionality for Silhouette library.
* `modules/web/app/utils/silhouette/SilhouetteWebController.scala`:  extends the SilhouetteController for the web project.
* `modules/admin/app/utils/silhouette/SilhouetteAdminController.scala`:  extends the SilhouetteController for the admin project.

* `modules/web/app/utils/silhouette/UserService.scala`:  simply retrieves a user from its corresponding LoginInfo.
* `modules/admin/app/utils/silhouette/ManagerService.scala`:  simply retrieves a manager from its corresponding LoginInfo.

* `modules/web/app/utils/silhouette/PasswordInfoWebDAO.scala`:  simply retrieves and saves a user's PasswordInfo from its corresponding LoginInfo.
* `modules/admin/app/utils/silhouette/PasswordInfoAdminDAO.scala`:  simply retrieves and saves a manager's PasswordInfo from its corresponding LoginInfo.

* `modules/web/app/utils/silhouette/TokenUserService.scala`:  implements the corresponding TokenService[TokenUser].
* `modules/admin/app/utils/silhouette/TokenManagerService.scala`:  implements the corresponding TokenService[TokenManager].


### Authentication

Please, check the Auth controller for the web service ( `modules/web/app/controllers/web/Auth.scala`) to know how to:

* Sign up (with email confirmation)
* Sign in
* Sign out
* Reset password (via email)

Run the app with simply

    $ run

And go to `www.myweb.com:9000`.

### Authorization

The authorization is implemented only for the admin web page. Each manager has one or more roles, and each role indicates a specific area or hierarchical level. And you can restrict sections to those managers who match with a set of roles (using logic `OR` or `AND`, you can choose). The master role has always full access to everywhere. For example:

* `social`: the manager works for the 'social' area.
* `sales` and `high`: the manager has a 'high' access and works for the 'sales' area.
* `master`: full access to every point of the Admin Webpage.

The Authorization objects are implemented in `modules/admin/app/utils/silhouette/Authorization.scala`.

* `WithRole(anyOf: String*)`: you can specifiy a list of roles using logic `OR`. So only those managers with __any__ of the specified roles will be allowed to access.
* `WithRoles(allOf: String*)`: you can specifiy a list of roles using logic `AND`. So only those managers with __all__ of the specified roles will be allowed to access.

You also have some tags to customise your UI according to the roles for the logged manager. They are within the folder `modules/admin/app/views/admin/tags/auth`.

* `withRole(anyOf: String*) { … }`
* `withRoleOrElse(anyOf: String*) { … } { … }`
* `withRoles(anyOf: String*) { … }`
* `withRolesOrElse(anyOf: String*) { … } { … }`

Run the app with simply

    $ run

And go to `admin.myweb.com:9000`. You will see a bit more information when you sign in and you will be able to try the authorization functionality by yourself.
