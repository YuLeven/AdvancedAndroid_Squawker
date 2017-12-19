package android.example.com.squawker.util;

/**
 * Created by Yuri Levenhagen on 2017-12-19 as part
 * of the Udacity-Google Advanced Android App Development course.
 * <p>
 * The base example code belongs to The Android Open Source Project under the Apache 2.0 licence
 * All code further implemented as part of the course is under the same licence.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class Util {

    /**
     * Clips (truncates) a message to a maximum length
     * @param message - The string to be clipped
     * @param maximumLength - The desired maximum length
     * @return - The clipped string
     */
    public static String clipString(String message, int maximumLength) {
        if (message.length() > maximumLength) {
            message = message.substring(0, maximumLength) + "\u2026";
        }
        return message;
    }
}
