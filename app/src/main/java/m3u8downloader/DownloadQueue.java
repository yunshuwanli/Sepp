package m3u8downloader;

import java.util.ArrayList;
import java.util.List;

import m3u8downloader.bean.M3U8Task;

class DownloadQueue {
    private List<M3U8Task> queue = new ArrayList();

    public void offer(M3U8Task m3U8Task) {
        this.queue.add(m3U8Task);
    }

    public M3U8Task poll() {
        try {
            if (this.queue.size() >= 2) {
                this.queue.remove(0);
                return (M3U8Task) this.queue.get(0);
            } else if (this.queue.size() == 1) {
                this.queue.remove(0);
            }
        } catch (Exception unused) {
            return null;
        }
        return null;
    }

    public M3U8Task peek() {
        try {
            if (this.queue.size() >= 1) {
                return (M3U8Task) this.queue.get(0);
            }
        } catch (Exception unused) {
            return null;
        }
        return null;
    }

    public boolean remove(M3U8Task m3U8Task) {
        return contains(m3U8Task) ? this.queue.remove(m3U8Task) : false;
    }

    public boolean contains(M3U8Task m3U8Task) {
        return this.queue.contains(m3U8Task);
    }

    public M3U8Task getTask(String str) {
        int i = 0;
        while (i < this.queue.size()) {
            try {
                if (((M3U8Task) this.queue.get(i)).getUrl().equals(str)) {
                    return (M3U8Task) this.queue.get(i);
                }
                i++;
            } catch (Exception unused) {
                return null;
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return this.queue.size();
    }

    public boolean isHead(String str) {
        return isHead(new M3U8Task(str));
    }

    public boolean isHead(M3U8Task m3U8Task) {
        return m3U8Task.equals(peek());
    }
}
