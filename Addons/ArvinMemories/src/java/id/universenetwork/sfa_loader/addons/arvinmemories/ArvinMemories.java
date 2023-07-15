package id.universenetwork.sfa_loader.addons.arvinmemories;

import id.universenetwork.sfa_loader.libraries.infinitylib.core.SlimefunAddonInstance;
import id.universenetwork.sfa_loader.template.AddonTemplate;

public class ArvinMemories extends AddonTemplate {
    @Override
    public void onLoad() {
        Group group = new Group(this);
        group.register(SlimefunAddonInstance.getSFAInstance());
    }
}
