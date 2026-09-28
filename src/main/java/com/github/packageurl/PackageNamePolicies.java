/*
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.packageurl;

import com.github.packageurl.internal.StringUtil;

/**
 * Resolves the name-normalisation policy for a package type.
 * Reusable policies are shared across types instead of one class per type.
 */
final class PackageNamePolicies {

    static final PackageNamePolicy PRESERVE = value -> value;

    static final PackageNamePolicy LOWERCASE = StringUtil::toLowerCase;

    static final PackageNamePolicy PUB =
            value -> LOWERCASE.normalize(value).replaceAll("[^a-z0-9_]", "_");

    static final PackageNamePolicy PYPI = value -> LOWERCASE.normalize(value).replace('_', '-');

    private PackageNamePolicies() {}

    static PackageNamePolicy forType(final String type) {
        switch (type) {
            case PackageURL.StandardTypes.APK:
            case PackageURL.StandardTypes.BITBUCKET:
            case PackageURL.StandardTypes.BITNAMI:
            case PackageURL.StandardTypes.COMPOSER:
            case PackageURL.StandardTypes.DEB:
            case PackageURL.StandardTypes.GITHUB:
            case PackageURL.StandardTypes.GOLANG:
            case PackageURL.StandardTypes.HEX:
            case PackageURL.StandardTypes.LUAROCKS:
            case PackageURL.StandardTypes.OCI:
                return LOWERCASE;
            case PackageURL.StandardTypes.PUB:
                return PUB;
            case PackageURL.StandardTypes.PYPI:
                return PYPI;
            default:
                return PRESERVE;
        }
    }
}
