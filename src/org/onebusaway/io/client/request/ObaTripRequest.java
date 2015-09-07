/*
 * Copyright (C) 2010 Paul Watts (paulcwatts@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onebusaway.io.client.request;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;

/**
 * Retrieve info about a specific trip.
 * {@link http://code.google.com/p/onebusaway/wiki/OneBusAwayRestApi_Trip}
 *
 * @author Paul Watts (paulcwatts@gmail.com)
 */
public final class ObaTripRequest extends RequestBase implements Callable<ObaTripResponse> {

    protected ObaTripRequest(URI uri) {
        super(uri);
    }

    public static class Builder extends RequestBase.BuilderBase {

        public Builder(String tripId) throws UnsupportedEncodingException {
            super(getPathWithId("/trip/", tripId));
        }

        public ObaTripRequest build() throws URISyntaxException {
            return new ObaTripRequest(buildUri());
        }
    }

    /**
     * Helper method for constructing new instances.
     *
     * @param context The package context.
     * @param tripId  The tripId to request.
     * @return The new request instance.
     * @throws UnsupportedEncodingException 
     * @throws URISyntaxException 
     */
    public static ObaTripRequest newRequest(String tripId) throws UnsupportedEncodingException, URISyntaxException {
        return new Builder(tripId).build();
    }

    @Override
    public ObaTripResponse call() {
        return call(ObaTripResponse.class);
    }

    @Override
    public String toString() {
        return "ObaTripRequest [mUri=" + mUri + "]";
    }
}