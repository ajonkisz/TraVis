/*
 * TraceInfo.java
 *
 * Copyright (C) 2011-2012, Artur Jonkisz, <travis.source@gmail.com>
 *
 * This file is part of TraVis.
 * See https://github.com/ajonkisz/TraVis for more info.
 *
 * TraVis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TraVis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with TraVis.  If not, see <http://www.gnu.org/licenses/>.
 */

package travis.model.script;

public class TraceInfo {

    private final int methodId;
    private final boolean returnCall;
    private final long callTime;
    private final long threadId;

    public TraceInfo(int methodId, boolean returnCall, long callTime,
                     long threadId) {
        this.methodId = methodId;
        this.returnCall = returnCall;
        this.callTime = callTime;
        this.threadId = threadId;
    }

    public int getMethodId() {
        return methodId;
    }

    public boolean isReturnCall() {
        return returnCall;
    }

    public long getCallTime() {
        return callTime;
    }

    public long getThreadId() {
        return threadId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(returnCall ? "<-- " : "--> ");
        sb.append(methodId);
        sb.append(' ');
        sb.append(callTime);
        sb.append(' ');
        sb.append(threadId);
        return sb.toString();
    }
}
