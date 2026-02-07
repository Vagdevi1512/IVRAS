// /*
//  * Licensed to the Apache Software Foundation (ASF) under one or more
//  *  contributor license agreements.  The ASF licenses this file to You
//  * under the Apache License, Version 2.0 (the "License"); you may not
//  * use this file except in compliance with the License.
//  * You may obtain a copy of the License at
//  *
//  *     http://www.apache.org/licenses/LICENSE-2.0
//  *
//  * Unless required by applicable law or agreed to in writing, software
//  * distributed under the License is distributed on an "AS IS" BASIS,
//  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  * See the License for the specific language governing permissions and
//  * limitations under the License.  For additional information regarding
//  * copyright in this work, please see the NOTICE file in the top level
//  * directory of this distribution.
//  */
// /* Created on Aug 12, 2003 */
// package org.apache.roller.weblogger.business.search.lucene;

// import org.apache.commons.logging.Log;
// import org.apache.commons.logging.LogFactory;

// /**
//  * An operation that writes to index.
//  * @author Mindaugas Idzelis (min@idzelis.com)
//  */
// public abstract class WriteToIndexOperation extends IndexOperation {
    
//     public WriteToIndexOperation(LuceneIndexManager mgr) {
//         super(mgr);
//     }
    
//     private static Log logger =
//             LogFactory.getFactory().getInstance(WriteToIndexOperation.class);
    
//     @Override
//     public void run() {
//         try {
//             manager.getReadWriteLock().writeLock().lock();
//             logger.debug("Starting search index operation");
//             doRun();
//             logger.debug("Search index operation complete");

//         } catch (Exception e) {
//             logger.error("Error acquiring write lock on index", e);
            
//         } finally {
//             manager.getReadWriteLock().writeLock().unlock();
//         }
//         manager.resetSharedReader();
//     }
// }


package org.apache.roller.weblogger.business.search.lucene;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class for index operations that modify the index.
 * Ensures execution under an exclusive write lock and
 * resets shared readers after completion.
 */
public abstract class WriteToIndexOperation extends IndexOperation {

    private static final Log logger =
            LogFactory.getFactory().getInstance(WriteToIndexOperation.class);

    protected WriteToIndexOperation(LuceneIndexManager mgr) {
        super(mgr);
    }

    /**
     * Hook for acquiring the write lock.
     */
    protected void acquireWriteLock() {
        manager.getReadWriteLock().writeLock().lock();
    }

    /**
     * Hook for releasing the write lock.
     */
    protected void releaseWriteLock() {
        manager.getReadWriteLock().writeLock().unlock();
    }

    /**
     * Hook for post-write cleanup.
     * Write operations must invalidate shared readers.
     */
    protected void afterWrite() {
        manager.resetSharedReader();
    }

    @Override
    public void run() {
        try {
            acquireWriteLock();
            logger.debug("Starting index write operation");
            doRun();
            logger.debug("Index write operation complete");

        } catch (Exception e) {
            logger.error("Error during write index operation", e);

        } finally {
            releaseWriteLock();
            afterWrite();
        }
    }
}
