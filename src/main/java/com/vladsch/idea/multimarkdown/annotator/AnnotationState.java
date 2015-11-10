/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the of the copyright holder.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package com.vladsch.idea.multimarkdown.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;

public class AnnotationState {
    final public static String TYPE_CHANGE_LINK_REF_QUICK_FIX = "ChangeLinkRefQuickFix";
    final public static String TYPE_CREATE_FILE_QUICK_FIX = "CreateFileQuickFix";
    final public static String TYPE_DELETE_WIKI_PAGE_REF_QUICK_FIX = "DeleteWikiPageRefQuickFix";
    final public static String TYPE_DELETE_WIKI_PAGE_TITLE_QUICK_FIX = "DeleteWikiPageTitleQuickFix";
    final public static String TYPE_RENAME_FILE_AND_RE_TARGET_QUICK_FIX = "RenameFileAndReTargetQuickFix";
    final public static String TYPE_RENAME_FILE_QUICK_FIX = "RenameFileQuickFix";
    final public static String TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX = "SwapWikiPageRefTitleQuickFix";

    final public static HashMap<String, Integer> typeParams = new HashMap<String, Integer>(10);
    static {
        typeParams.put(TYPE_CHANGE_LINK_REF_QUICK_FIX, 1);
        typeParams.put(TYPE_CREATE_FILE_QUICK_FIX, 1);
        typeParams.put(TYPE_DELETE_WIKI_PAGE_REF_QUICK_FIX, 0);
        typeParams.put(TYPE_DELETE_WIKI_PAGE_TITLE_QUICK_FIX, 0);
        typeParams.put(TYPE_RENAME_FILE_AND_RE_TARGET_QUICK_FIX, 2);
        typeParams.put(TYPE_RENAME_FILE_QUICK_FIX, 2);
        typeParams.put(TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX, 0);
    }

    public final AnnotationHolder holder;
    public Annotation annotator = null;
    public boolean warningsOnly = true;
    public boolean canCreateFile = true;
    public boolean needTargetList = true;
    protected HashMap<String, HashSet<String>> alreadyOffered = new HashMap<String, HashSet<String>>();

    public AnnotationState(AnnotationHolder holder) {
        this.holder = holder;
    }

    public boolean hadAnnotation() {
        return annotator != null;
    }

    public boolean alreadyOfferedIds(@NotNull String type, @NotNull String... ids) {
        if (!alreadyOffered.containsKey(type)) return false;
        HashSet<String> idSet = alreadyOffered.get(type);
        for (String id : ids) {
            if (!idSet.contains(id)) return false;
        }
        return true;
    }

    public boolean alreadyOfferedTypes(@NotNull String... types) {
        for (String type : types) {
            if (!alreadyOffered.containsKey(type)) return false;
        }
        return true;
    }

    public static int sumLength(@NotNull String[] array) {
        int sum = 0;
        for (String s : array) {
            if (s != null) {
                sum += s.length();
            }
        }
        return sum;
    }

    public static String implode(@NotNull String separator, String... array) {
        StringBuilder result = new StringBuilder(array.length * separator.length() + sumLength(array));
        for (String s : array) {
            if (s != null) {
                if (result.length() > 0) {
                    result.append(separator);
                }
                result.append(s);
            }
        }
        return result.toString();
    }

    public boolean addingAlreadyOffered(@NotNull String type, String... idList) {
        assert typeParams.containsKey(type) : "quickFix type " + type + " is not defined in parameter map";
        assert typeParams.get(type) == idList.length : "quickFix type " + type + " should have " + typeParams.get(type) + ", given " + idList.length;

        String id;
        if (idList.length == 0) {
            if (!alreadyOfferedTypes(type)) {
                addAlreadyOffered(type);
                return true;
            }
            return false;
        } else if (idList.length == 1) id = idList[0];
        else id = implode("|", idList);

        if (!alreadyOfferedIds(type, id)) {
            addAlreadyOffered(type, id);
            return true;
        }
        return false;
    }

    public boolean alreadyOfferedId(String type, String... idList) {
        assert typeParams.containsKey(type) : "quickFix type " + type + " is not defined in parameter map";
        assert typeParams.get(type) == idList.length : "quickFix type " + type + " should have " + typeParams.get(type) + ", given " + idList.length;

        String id;
        if (idList.length == 0) return !alreadyOfferedTypes(type);
        else if (idList.length == 1) id = idList[0];
        else id = implode("|", idList);

        return !alreadyOfferedIds(type, id);
    }

    public void addAlreadyOffered(@NotNull String type, @NotNull String id) {
        if (!alreadyOffered.containsKey(type)) {
            alreadyOffered.put(type, new HashSet<String>(1));
        }

        alreadyOffered.get(type).add(id);
    }

    public void addAlreadyOffered(@NotNull String type) {
        if (!alreadyOffered.containsKey(type)) {
            alreadyOffered.put(type, new HashSet<String>(1));
        }
    }

    public HashSet<String> getAlreadyOffered(String type) {
        addAlreadyOffered(type);
        return alreadyOffered.get(type);
    }

    public int getAlreadyOfferedSize(String type) {
        return !alreadyOffered.containsKey(type) ? 0 : alreadyOffered.get(type).size();
    }
}