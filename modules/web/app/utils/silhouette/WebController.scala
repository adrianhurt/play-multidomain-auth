package utils.silhouette

import models.{ User, MailTokenUser }

trait WebController extends AuthenticationController[User, MailTokenUser]