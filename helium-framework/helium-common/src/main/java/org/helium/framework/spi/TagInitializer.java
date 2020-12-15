package org.helium.framework.spi;

import org.helium.framework.entitys.TagNode;
import org.helium.framework.tag.Tag;
import org.helium.framework.tag.TagMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Coral on 8/11/15.
 */
public class TagInitializer {
	public static void applyTags(TagMode mode, Object obj) throws Exception {
		List<Tag> tags = new ArrayList<>();
		List<TagNode> tagNodes = AnnotationResolver.resolveTags(obj.getClass(), null, null);
		for (TagNode node : tagNodes) {
			Tag tag = (Tag) ObjectCreator.createObject(node.getClazz());
			tag.initWithConfig(obj, node);
			tags.add(tag);
		}
		for (Tag tag: tags) {
			if (tag.getModes().contains(mode)) {
				tag.applyTag(mode);
			}
		}
	}
}
