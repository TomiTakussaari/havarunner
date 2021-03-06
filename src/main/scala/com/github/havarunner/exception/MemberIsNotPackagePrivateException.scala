package com.github.havarunner.exception

import java.lang.reflect.{Modifier, Member}
import com.github.havarunner.exception.MemberIsNotPackagePrivateException._

class MemberIsNotPackagePrivateException(member: Member) extends CodingConventionException(
  modifierToString(member)
)

object MemberIsNotPackagePrivateException {
  def modifierToString(member: Member) =
    if (Modifier.isPrivate(member.getModifiers)) {
      "private"
    } else if (Modifier.isPublic(member.getModifiers)) {
      "public"
    } else if (Modifier.isProtected(member.getModifiers)) {
      "protected"
    } else {
      throw new RuntimeException("unrecognised modifier for " + member)
    }
}
