/*
 * CivBuddy - A calculator app for players of Francis Tresham's original Civilization board game (1980)
 * Copyright (C) 2012-2023 Thomas Jensen
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */

import appVersionJson from './version.json';


/**
 * Describes the contents of the version.json file.
 */
interface AppVersionJson {
    /** application name as specified in package.json */
    readonly name: string;

    /** build timestamp in milliseconds since the epoch */
    readonly buildDate: number;

    /** application version as specified in package.json */
    readonly version: string;

    /** number of commits in the Git repo */
    readonly numCommits: number;

    /** latest Git commit hash */
    readonly hash: string;

    /** flag is set when uncommitted or untracked changes are present in the workspace */
    readonly dirty: boolean;
}



/**
 * Provides data on the build version number and some more build metadata.
 */
export class AppVersion {
    private readonly appVersion: AppVersionJson = appVersionJson as any;

    /** Get application name as specified in *package.json*. */
    public getName(): string {
        return this.appVersion.name;
    }

    /** Get build timestamp. */
    public getBuildDate(): Date {
        return new Date(this.appVersion.buildDate);
    }

    /** Get application version as specified in *package.json*. */
    public getVersion(): string {
        return this.appVersion.version;
    }

    /** Get number of commits in the Git repo. */
    public getNumCommits(): number {
        return this.appVersion.numCommits;
    }

    /** Get latest Git commit hash. */
    public getHash(): string {
        return this.appVersion.hash;
    }

    /** Flag is set when uncommitted or untracked changes are present in the workspace. */
    public isDirty(): boolean {
        return this.appVersion.dirty;
    }

    public getCombinedVersion(): string {
        return this.appVersion.version + '.' + this.appVersion.numCommits + ' (' + this.appVersion.hash + ')';
    }
}
