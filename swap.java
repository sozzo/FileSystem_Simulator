public class swap {
    /**
     * The name of this program.
     * This is the program name that is used
     * when displaying error messages.
     */
    public static final String PROGRAM_NAME = "swap" ;

    /**
     * The size of the buffer to be used when reading files.
     */
    public static final int BUF_SIZE = 4096 ;

    /**
     * The file mode to use when creating the output file.
     */
    // ??? perhaps this should be the same mode as the input file
    public static final short OUTPUT_MODE = 0700 ;

    /**
     * Swaps filepaths of two files.
     * @exception java.lang.Exception if an exception is thrown by
     * an underlying operation
     */
    public static void main( String[] argv ) throws Exception
    {
        // initialize the file system simulator kernel
        Kernel.initialize() ;

        // make sure we got the correct number of parameters
        if( argv.length != 2 )
        {
            System.err.println( PROGRAM_NAME + ": usage: java " +
                    PROGRAM_NAME + " input-file output-file" ) ;
            Kernel.exit( 1 ) ;
        }

        // give the parameters more meaningful names

        // make sure we got the correct number of parameters
        if( argv.length != 2 )
        {
            System.err.println( PROGRAM_NAME + ": usage: java " +
                    PROGRAM_NAME + " input-file output-file" ) ;
            Kernel.exit( 1 ) ;
        }

        // give the parameters more meaningful names
        String filename1 = argv[0] ;
        String filename2 = argv[1] ;
        String swap_filename = "/swap_file";

        // open the input file
        int fd1 = Kernel.open( filename1 , Kernel.O_RDWR ) ;
        if( fd1 < 0 )
        {
            Kernel.perror( PROGRAM_NAME ) ;
            System.err.println( PROGRAM_NAME + ": unable to open input file \"" +
                    filename1 + "\"" ) ;
            Kernel.exit( 2 ) ;
        }

        // open the output file
        int fd2 = Kernel.open( filename2 , Kernel.O_RDWR ) ;
        if( fd2 < 0 )
        {
            Kernel.perror( PROGRAM_NAME ) ;
            System.err.println( PROGRAM_NAME + ": unable to open output file \"" +
                    argv[1] + "\"" ) ;
            Kernel.exit( 3 ) ;
        }

        // open swap file
        int swap_fd = Kernel.creat( swap_filename , (short)Kernel.O_RDWR ) ;
        if( swap_fd < 0 ) {
            Kernel.perror( PROGRAM_NAME ) ;
            System.err.println( PROGRAM_NAME + ": unable to open swap file" ) ;
            Kernel.exit( 4 ) ;
        }

        copy_file_contents(fd1, swap_fd);
        
        Kernel.close(fd1);
        Kernel.clear( filename1 );
        fd1 = Kernel.open( filename1 , Kernel.O_RDWR ) ;
        
        copy_file_contents(fd2, fd1);
        
        Kernel.close(fd2);
        Kernel.clear( filename2 );
        
        fd2 = Kernel.open( filename2 , Kernel.O_RDWR ) ;
        
        Kernel.close(swap_fd);
        swap_fd = Kernel.open( swap_filename , Kernel.O_RDWR ) ;
        copy_file_contents(swap_fd, fd2);

        // close the files
        Kernel.close( fd1 ) ;
        Kernel.close( fd2 ) ;
        Kernel.close( swap_fd ) ;

        Kernel.exit( 0 ) ;
    }

    static void copy_file_contents(int in_fd, int out_fd) throws Exception {
        int rd_count ;
        byte[] buffer = new byte[BUF_SIZE] ;
        while( true ) {
            // read a buffer full from the input file

            rd_count = Kernel.read(in_fd, buffer, BUF_SIZE);
            System.out.println(rd_count);
            // if error or nothing read, quit the loop
            if (rd_count <= 0)
                break;

            // write whatever we read to the output file
            int wr_count = Kernel.write(out_fd, buffer, rd_count);

            // if error or nothing written, give message and exit
            if (wr_count <= 0) {
                Kernel.perror(PROGRAM_NAME);
                System.err.println(PROGRAM_NAME +
                        ": error during write to output file");
                Kernel.exit(5);
            }
        }
    }
}

