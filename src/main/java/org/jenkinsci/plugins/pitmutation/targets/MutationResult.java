package org.jenkinsci.plugins.pitmutation.targets;

import com.google.common.collect.Ordering;
import hudson.model.AbstractBuild;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Ed Kimber
 */
public abstract class MutationResult implements Comparable {

  public MutationResult(String name, MutationResult parent) {
    parent_ = parent;
    name_ = name;
    owner_ = parent == null ? null : parent.getOwner();
  }

  public String getName() {
    return name_;
  }

  public MutationResult getParent() {
    return parent_;
  }

  public MutationResult getPreviousResult() {
    if (parent_ == null) {
      return null;
    }
    else {
      MutationResult previous = parent_.getPreviousResult();
      return previous == null ? null : previous.getChildMap().get(name_);
    }
  }

  public abstract String getDisplayName();

  public abstract MutationStats getMutationStats();

  public abstract Map<String, ? extends MutationResult> getChildMap();

  public boolean isSourceLevel() {
    return false;
  }

  public String getSourceFileContent() {
    return "";
  }

  public boolean isCoverageAltered() {
    return false;
  }

  public Collection<? extends MutationResult> getChildren() {
    return Ordering.natural().reverse().sortedCopy(getChildMap().values());
  }

  public int compareTo(Object o) {
    return getMutationStats().getUndetected() - ((MutationResult) o).getMutationStats().getUndetected();
  }

  public MutationStats getStatsDelta() {
    MutationResult previous = getPreviousResult();
    return previous == null ? getMutationStats() : getMutationStats().delta(previous.getMutationStats());
  }

  public Object getDynamic(String token, StaplerRequest req, StaplerResponse rsp) throws IOException {
    for (String name : getChildMap().keySet()) {
      if (urlTransform(name).equalsIgnoreCase(token)) {
        return getChildMap().get(name);
      }
    }
    return "#";
  }

  public AbstractBuild<?,?> getOwner() {
    return owner_;
  }

  public String getUrl() {
    return urlTransform(getName());
  }

  public List<MutationResult> getParents() {
    List<MutationResult> result = new ArrayList<MutationResult>();
    MutationResult p = getParent();
    while (p != null) {
      result.add(p);
      p = p.getParent();
    }
    Collections.reverse(result);
    return result;
  }

  public static String xmlTransform(String name) {
    return name.replaceAll("\\&", "&amp;").replaceAll("\\<", "&lt;").replaceAll("\\>", "&gt;");
  }

  public String relativeUrl(MutationResult parent) {
    StringBuilder url = new StringBuilder("..");
    MutationResult p = this.getParent();
    while (p != null && p != parent) {
      url.append("/..");
      p = p.getParent();
    }
    return url.toString();
  }

  static String urlTransform(String token) {
    StringBuilder buf = new StringBuilder(token.length());
    for (int i = 0; i < token.length(); i++) {
      final char c = token.charAt(i);
      if (('0' <= c && '9' >= c)
              || ('A' <= c && 'Z' >= c)
              || ('a' <= c && 'z' >= c)) {
        buf.append(c);
      } else {
        buf.append('_');
      }
    }
    return buf.toString();
  }

  static final Logger logger_ = Logger.getLogger(MutationResult.class.getName());

  private AbstractBuild owner_;
  private MutationResult parent_;
  private String name_;
}
