/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.shrinkwrap.tar.impl.exporter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.exporter.ArchiveExportException;
import org.jboss.shrinkwrap.api.exporter.FileExistsException;
import org.jboss.shrinkwrap.impl.base.exporter.AbstractExporterDelegate;
import org.jboss.shrinkwrap.impl.base.exporter.AbstractStreamExporterImpl;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.jboss.shrinkwrap.tar.api.exporter.TarGzExporter;

/**
 * Implementation of {@link TarGzExporter} used to export an Archive as a TAR format
 * encoded in GZIP. 
 * 
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class TarGzExporterImpl extends AbstractStreamExporterImpl implements TarGzExporter
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   @SuppressWarnings("unused")
   private static final Logger log = Logger.getLogger(TarGzExporterImpl.class.getName());

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates a new exporter for the specified archive
    */
   public TarGzExporterImpl(final Archive<?> archive)
   {
      super(archive);
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.exporter.StreamExporter#export()
    */
   @Override
   public InputStream export()
   {
      // Create export delegate
      final AbstractExporterDelegate<InputStream> exportDelegate = new TarGzExporterDelegate(this.getArchive());

      // Execute export
      return exportDelegate.export();
   }

   
   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.exporter.StreamExporter#export(java.io.OutputStream)
    */
   @Override
   public void export(final OutputStream target) throws ArchiveExportException, IllegalArgumentException
   {
      // Precondition checks
      if (target == null)
      {
         throw new IllegalArgumentException("Target must be specified");
      }

      // Get Stream
      final InputStream in = this.export();

      // Write out
      try
      {
         IOUtil.copyWithClose(in, target);
      }
      catch (final IOException e)
      {
         throw new ArchiveExportException("Error encountered in exporting archive to " + target, e);
      }
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.tar.api.exporter.TarGzExporter#exportTarGz(java.io.File, boolean)
    */
   @Override
   public void exportTarGz(final File target, final boolean overwrite) throws ArchiveExportException,
         FileExistsException, IllegalArgumentException
   {
      // Get stream and perform precondition checks
      final OutputStream out = this.getOutputStreamToFile(target, overwrite);

      // Write out
      this.export(out);
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.tar.api.exporter.TarGzExporter#exportTarGz(java.io.File)
    */
   @Override
   public void exportTarGz(final File target) throws ArchiveExportException, FileExistsException,
         IllegalArgumentException
   {
      this.exportTarGz(target, false);
   }

}
