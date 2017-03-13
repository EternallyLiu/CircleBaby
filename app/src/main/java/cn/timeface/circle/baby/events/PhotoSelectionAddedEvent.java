/*
 * Copyright 2013 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.timeface.circle.baby.events;


import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.support.api.models.objs.ImgObj;

public class PhotoSelectionAddedEvent {

    private final List<ImgObj> mUploads;

    public PhotoSelectionAddedEvent(List<ImgObj> uploads) {
        mUploads = uploads;
    }

    public PhotoSelectionAddedEvent(ImgObj upload) {
        mUploads = new ArrayList<ImgObj>();
        mUploads.add(upload);
    }

    public List<ImgObj> getTargets() {
        return mUploads;
    }

    public ImgObj getTarget() {
        if (isSingleChange()) {
            return mUploads.get(0);
        } else {
            throw new IllegalStateException("Can only call this when isSingleChange returns true");
        }
    }

    public boolean isSingleChange() {
        return mUploads.size() == 1;
    }

}
