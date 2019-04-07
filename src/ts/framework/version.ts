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
