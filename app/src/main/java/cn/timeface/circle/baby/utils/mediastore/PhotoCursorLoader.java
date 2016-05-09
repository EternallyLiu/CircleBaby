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
package cn.timeface.circle.baby.utils.mediastore;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

public class PhotoCursorLoader extends CursorLoader {

    private final boolean contentChanged;

    public PhotoCursorLoader(Context context, Uri uri, String[] projection, String selection,
                             String[] selectionArgs,
                             String sortOrder, boolean requeryOnChange) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
        contentChanged = requeryOnChange;
    }

    @Override
    public void onContentChanged() {
        if (contentChanged) {
            super.onContentChanged();
        }
    }

}
