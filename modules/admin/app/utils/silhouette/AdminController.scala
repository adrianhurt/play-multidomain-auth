package utils.silhouette

import models.{ Manager, MailTokenManager }

trait AdminController extends AuthenticationController[Manager, MailTokenManager]