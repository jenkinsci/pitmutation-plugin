package org.jenkinsci.plugins.pitmutation;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.StaplerRequest;

/**
 * The type Descriptor.
 */
@Extension
@Symbol("pitmutation")
public class DescriptorImpl extends BuildStepDescriptor<Publisher> {

    /**
     * Instantiates a new Descriptor.
     */
    public DescriptorImpl() {
        super(PitPublisher.class);
    }

    @Override
    public String getDisplayName() {
        return Messages.PitPublisher_DisplayName();
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> aClass) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
        req.bindParameters(this, "pitmutation");
        save();
        return super.configure(req, formData);
    }
}
